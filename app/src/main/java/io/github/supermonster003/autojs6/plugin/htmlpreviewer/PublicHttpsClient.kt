package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import okhttp3.Dns
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.FilterInputStream
import java.io.IOException
import java.io.InputStream
import java.net.InetAddress
import java.net.Proxy
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/** The returned addresses are the addresses OkHttp connects to, with normal TLS host validation. */
internal class PublicAddressDns(private val delegate: Dns = Dns.SYSTEM) : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        val addresses = delegate.lookup(hostname)
        if (addresses.isEmpty() || addresses.any { !isPublicAddress(it) }) {
            throw UnknownHostException("Remote resource does not resolve exclusively to public addresses")
        }
        return addresses
    }

    companion object {
        fun isPublicAddress(address: InetAddress): Boolean {
            if (address.isAnyLocalAddress || address.isLoopbackAddress || address.isLinkLocalAddress ||
                address.isSiteLocalAddress || address.isMulticastAddress) return false
            val bytes = address.address.map { it.toInt() and 255 }
            if (bytes.size == 4) {
                val (a, b, c) = bytes
                return !(a == 0 || a == 10 || a == 127 || a >= 224 ||
                    (a == 100 && b in 64..127) || (a == 169 && b == 254) ||
                    (a == 172 && b in 16..31) || (a == 192 && b == 168) ||
                    (a == 192 && b == 0) || (a == 192 && b == 88 && c == 99) ||
                    (a == 198 && b in 18..19) || (a == 198 && b == 51 && c == 100) ||
                    (a == 203 && b == 0 && c == 113))
            }
            // Only global unicast IPv6, excluding protocol assignments, documentation and 6to4.
            return bytes.size == 16 && bytes[0] in 0x20..0x3f &&
                !(bytes[0] == 0x20 && bytes[1] == 0x01 && bytes[2] <= 1) &&
                !(bytes.take(4) == listOf(0x20, 0x01, 0x0d, 0xb8)) &&
                !(bytes[0] == 0x20 && bytes[1] == 0x02) &&
                !(bytes[0] == 0x3f && bytes[1] == 0xff && bytes[2] < 0x10)
        }
    }
}

internal class PublicHttpsClient(
    private val allowedUrl: (String) -> Boolean,
    private val transport: (Request) -> Response = defaultTransport,
) {
    /** WebView exposes no request body. Only bodyless resource loads are supported. */
    fun open(url: String, method: String, headers: Map<String, String>): PublicHttpsResource {
        if (method != "GET" && method != "HEAD") throw IOException("Unsupported resource method")
        var current = url
        repeat(MAX_REDIRECTS + 1) { redirect ->
            if (!allowedUrl(current)) throw IOException("Blocked remote resource URL")
            val request = Request.Builder().url(current).method(method, null).apply {
                headers.forEach { (name, value) ->
                    if (name.lowercase() in REQUEST_HEADERS) header(name, value)
                }
            }.build()
            val response = transport(request)
            if (response.code in REDIRECT_CODES) {
                response.use {
                    if (redirect == MAX_REDIRECTS) throw IOException("Too many resource redirects")
                    val location = response.header("Location") ?: throw IOException("Missing redirect target")
                    current = response.request.url.resolve(location)?.toString()
                        ?: throw IOException("Invalid redirect target")
                }
            } else {
                if (response.code !in 200..299 || response.body == null) {
                    response.close()
                    throw IOException("Remote resource request failed")
                }
                val body = response.body!!
                if (body.contentLength() > MAX_RESOURCE_BYTES) {
                    response.close()
                    throw IOException("Remote resource is too large")
                }
                val mediaType = body.contentType()
                val responseHeaders = response.headers.toMultimap().filterKeys {
                    it.lowercase() in RESPONSE_HEADERS
                }.mapValues { it.value.joinToString(", ") } + mapOf("Cache-Control" to "no-store")
                return PublicHttpsResource(
                    mediaType?.let { "${it.type}/${it.subtype}" } ?: "application/octet-stream",
                    mediaType?.charset()?.name(), response.code, reasonPhrase(response), responseHeaders,
                    LimitedResourceStream(body.byteStream(), MAX_RESOURCE_BYTES) { response.close() },
                )
            }
        }
        throw IOException("Too many resource redirects")
    }

    companion object {
        private fun reasonPhrase(response: Response): String = response.message.takeIf { phrase ->
            phrase.isNotBlank() && phrase.all { it.code in 32..126 }
        } ?: when (response.code) {
            200 -> "OK"
            201 -> "Created"
            202 -> "Accepted"
            204 -> "No Content"
            205 -> "Reset Content"
            206 -> "Partial Content"
            else -> "Success"
        }

        internal const val MAX_REDIRECTS = 5
        internal const val MAX_RESOURCE_BYTES = 32L * 1024 * 1024
        private val REDIRECT_CODES = setOf(301, 302, 303, 307, 308)
        private val REQUEST_HEADERS = setOf("accept", "accept-language", "origin", "range", "user-agent")
        private val RESPONSE_HEADERS = setOf(
            "access-control-allow-origin", "access-control-expose-headers", "content-range",
            "accept-ranges", "content-security-policy", "x-content-type-options",
        )
        private val client = OkHttpClient.Builder()
            .dns(PublicAddressDns())
            .proxy(Proxy.NO_PROXY)
            .followRedirects(false)
            .followSslRedirects(false)
            .retryOnConnectionFailure(false)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
        private val defaultTransport: (Request) -> Response = { client.newCall(it).execute() }
    }
}

internal data class PublicHttpsResource(
    val mimeType: String,
    val encoding: String?,
    val statusCode: Int,
    val reasonPhrase: String,
    val headers: Map<String, String>,
    val body: InputStream,
)

internal class LimitedResourceStream(
    input: InputStream,
    private val limit: Long,
    private val closeResponse: () -> Unit,
) : FilterInputStream(input) {
    private var consumed = 0L
    private var closed = false

    private fun record(count: Int): Int {
        if (count > 0) consumed += count
        if (consumed > limit) {
            close()
            throw IOException("Remote resource exceeded its size limit")
        }
        return count
    }

    override fun read(): Int = `in`.read().also { if (it >= 0) record(1) }
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int =
        record(`in`.read(buffer, offset, minOf(length.toLong(), limit - consumed + 1).toInt()))
    override fun skip(count: Long): Long {
        var left = count.coerceAtLeast(0)
        val buffer = ByteArray(minOf(left, 8192).toInt())
        while (left > 0) {
            val read = read(buffer, 0, minOf(left, buffer.size.toLong()).toInt())
            if (read < 0) break
            left -= read
        }
        return count.coerceAtLeast(0) - left
    }
    override fun markSupported() = false
    override fun reset(): Unit = throw IOException("Resource stream does not support reset")
    override fun close() {
        if (!closed) {
            closed = true
            try { super.close() } finally { closeResponse() }
        }
    }
}

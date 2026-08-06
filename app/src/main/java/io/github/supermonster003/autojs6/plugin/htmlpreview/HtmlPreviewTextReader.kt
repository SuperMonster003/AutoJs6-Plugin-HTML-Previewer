package io.github.supermonster003.autojs6.plugin.htmlpreview

import android.content.ContentResolver
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets

internal class HtmlPreviewTextReader(
    private val contentResolver: ContentResolver,
) {

    fun read(uri: Uri, maxBytes: Int): String {
        require(maxBytes > 0) { "maxBytes must be positive" }
        require(uri.scheme.equals(ContentResolver.SCHEME_CONTENT, ignoreCase = true)) {
            "Document URI must use the content scheme"
        }
        val bytes = contentResolver.openInputStream(uri)?.use { input ->
            HtmlPreviewTextCodec.readBounded(input, maxBytes)
        } ?: throw IOException("Cannot open the preview document")
        return HtmlPreviewTextCodec.decode(bytes)
    }
}

internal object HtmlPreviewTextCodec {

    fun readBounded(input: InputStream, maxBytes: Int): ByteArray {
        require(maxBytes > 0) { "maxBytes must be positive" }
        val output = ByteArrayOutputStream(minOf(DEFAULT_BUFFER_SIZE, maxBytes))
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var total = 0
        while (true) {
            val read = input.read(buffer)
            if (read < 0) break
            if (read == 0) {
                val oneByte = input.read()
                if (oneByte < 0) break
                total++
                if (total > maxBytes) throw HtmlPreviewTooLargeException(maxBytes)
                output.write(oneByte)
                continue
            }
            total += read
            if (total > maxBytes) throw HtmlPreviewTooLargeException(maxBytes)
            output.write(buffer, 0, read)
        }
        return output.toByteArray()
    }

    fun decode(bytes: ByteArray): String {
        val encoding = BOM_ENCODINGS.firstOrNull { (_, signature) -> bytes.startsWith(signature) }
        val charset = encoding?.first ?: StandardCharsets.UTF_8
        val offset = encoding?.second?.size ?: 0
        val decoder = charset.newDecoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE)
        return decoder.decode(ByteBuffer.wrap(bytes, offset, bytes.size - offset)).toString()
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean =
        size >= prefix.size && prefix.indices.all { this[it] == prefix[it] }

    private const val DEFAULT_BUFFER_SIZE = 8 * 1024

    private val BOM_ENCODINGS = listOf(
        charset("UTF-32BE") to byteArrayOf(0x00, 0x00, 0xFE.toByte(), 0xFF.toByte()),
        charset("UTF-32LE") to byteArrayOf(0xFF.toByte(), 0xFE.toByte(), 0x00, 0x00),
        StandardCharsets.UTF_8 to byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()),
        StandardCharsets.UTF_16BE to byteArrayOf(0xFE.toByte(), 0xFF.toByte()),
        StandardCharsets.UTF_16LE to byteArrayOf(0xFF.toByte(), 0xFE.toByte()),
    )

    private fun charset(name: String) = runCatching { java.nio.charset.Charset.forName(name) }
        .getOrDefault(StandardCharsets.UTF_8)
}

internal class HtmlPreviewTooLargeException(
    val limitBytes: Int,
) : IOException("HTML exceeds the $limitBytes-byte preview limit")

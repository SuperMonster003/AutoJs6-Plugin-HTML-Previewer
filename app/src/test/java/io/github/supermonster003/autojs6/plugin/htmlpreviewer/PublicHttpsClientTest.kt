package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import okhttp3.Dns
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.*
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.IOException
import java.net.InetAddress
import java.net.UnknownHostException

class PublicHttpsClientTest {
    private fun dns(resolve: () -> List<InetAddress>) = object : Dns {
        override fun lookup(hostname: String) = resolve()
    }

    private fun address(value: String) = InetAddress.getByName(value)

    @Test fun rejectsPrivateReservedAndMixedDnsAnswers() {
        val blocked = listOf("0.0.0.0", "10.1.2.3", "127.0.0.1", "169.254.169.254", "172.16.0.1",
            "192.168.1.1", "100.64.0.1", "192.0.2.1", "198.18.0.1", "198.51.100.1",
            "203.0.113.1", "224.0.0.1", "::1", "fc00::1", "fe80::1", "2001:db8::1", "2002:7f00:1::")
        for (ip in blocked) {
            assertFalse(ip, PublicAddressDns.isPublicAddress(address(ip)))
            assertThrows(UnknownHostException::class.java) {
                PublicAddressDns(dns { listOf(address("8.8.8.8"), address(ip)) }).lookup("example.test")
            }
        }
        assertThrows(UnknownHostException::class.java) { PublicAddressDns(dns { emptyList() }).lookup("example.test") }
    }

    @Test fun returnsExactlyValidatedAddressesWithoutSecondResolution() {
        var queries = 0
        val expected = listOf(address("8.8.8.8"), address("2606:4700:4700::1111"))
        val dns = PublicAddressDns(dns { queries++; expected })
        assertSame(expected, dns.lookup("example.test"))
        assertEquals(1, queries)
    }

    @Test fun rejectsRebindingOnNextLookup() {
        var queries = 0
        val dns = PublicAddressDns(dns { listOf(address(if (queries++ == 0) "8.8.8.8" else "127.0.0.1")) })
        dns.lookup("example.test")
        assertThrows(UnknownHostException::class.java) { dns.lookup("example.test") }
    }

    private fun response(request: Request, code: Int = 200, location: String? = null): Response =
        Response.Builder().request(request).protocol(Protocol.HTTP_1_1).code(code).message("Test")
            .body("image".toResponseBody()).apply { location?.let { header("Location", it) } }.build()

    @Test fun preservesPartialContentStatusAndRangeWithAnHttp2ReasonFallback() {
        val client = PublicHttpsClient({ true }) { request ->
            assertEquals("bytes=2-4", request.header("Range"))
            Response.Builder().request(request).protocol(Protocol.HTTP_2).code(206).message("")
                .header("Content-Range", "bytes 2-4/10").body("234".toResponseBody()).build()
        }
        val resource = client.open("https://public.example/media", "GET", mapOf("Range" to "bytes=2-4"))
        resource.body.use {
            assertEquals(206, resource.statusCode)
            assertEquals("Partial Content", resource.reasonPhrase)
            assertEquals("bytes 2-4/10", resource.headers.entries.single { it.key.equals("Content-Range", true) }.value)
            assertEquals("234", it.reader().readText())
        }
    }

    @Test fun preservesLegalSuccessStatusAndSanitizesNonAsciiReasonPhrases() {
        for ((message, expected) in listOf("Accepted by server" to "Accepted by server", "成功" to "Accepted")) {
            val client = PublicHttpsClient({ true }) { request ->
                response(request, 202).newBuilder().message(message).build()
            }
            val resource = client.open("https://public.example/resource", "GET", emptyMap())
            resource.body.use {
                assertEquals(202, resource.statusCode)
                assertEquals(expected, resource.reasonPhrase)
            }
        }
    }

    @Test fun validatesEveryRedirectBeforeSendingItAndDropsCredentials() {
        val visited = mutableListOf<String>()
        val client = PublicHttpsClient({ it.startsWith("https://public.example/") }) { request ->
            visited += request.url.toString()
            assertNull(request.header("Authorization")); assertNull(request.header("Cookie"))
            response(request, 302, "https://127.0.0.1/private")
        }
        assertThrows(IOException::class.java) {
            client.open("https://public.example/start", "GET", mapOf("Authorization" to "secret", "Cookie" to "secret"))
        }
        assertEquals(listOf("https://public.example/start"), visited)
    }

    @Test fun followsRelativeRedirectAndClosesSuccessfulBody() {
        var calls = 0
        val client = PublicHttpsClient({ it.startsWith("https://public.example/") }) { request ->
            if (calls++ == 0) response(request, 302, "/image") else response(request)
        }
        client.open("https://public.example/start", "GET", emptyMap()).body.use {
            assertEquals("image", it.reader().readText())
        }
        assertEquals(2, calls)
    }

    @Test fun rejectsUnsafeMethodAndBoundsRedirectLoops() {
        var calls = 0
        val client = PublicHttpsClient({ true }) { request -> calls++; response(request, 307, "/again") }
        assertThrows(IOException::class.java) { client.open("https://public.example/", "POST", emptyMap()) }
        assertEquals(0, calls)
        assertThrows(IOException::class.java) { client.open("https://public.example/", "GET", emptyMap()) }
        assertEquals(PublicHttpsClient.MAX_REDIRECTS + 1, calls)
    }

    @Test fun streamLimitCannotBeBypassedByBulkReadsOrSkipAndClosesOnce() {
        for (skip in listOf(false, true)) {
            var closes = 0
            val stream = LimitedResourceStream(ByteArrayInputStream(ByteArray(12)), 10) { closes++ }
            assertThrows(IOException::class.java) {
                if (skip) stream.skip(12) else stream.read(ByteArray(12))
            }
            stream.close()
            assertEquals(1, closes)
        }
    }
}

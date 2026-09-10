package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.StandardCharsets

class HtmlPreviewerTextCodecTest {

    @Test
    fun boundedReaderAcceptsTheExactLimit() {
        val bytes = ByteArray(32) { it.toByte() }
        assertArrayEquals(bytes, HtmlPreviewerTextCodec.readBounded(ByteArrayInputStream(bytes), bytes.size))
    }

    @Test(expected = HtmlPreviewerTooLargeException::class)
    fun boundedReaderRejectsTheFirstByteOverTheLimit() {
        HtmlPreviewerTextCodec.readBounded(ByteArrayInputStream(ByteArray(33)), 32)
    }

    @Test
    fun boundedReaderMakesProgressAfterAZeroLengthRead() {
        val expected = "preview".toByteArray()
        val input = object : InputStream() {
            private val delegate = ByteArrayInputStream(expected)
            private var firstBulkRead = true

            override fun read(): Int = delegate.read()

            override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
                if (firstBulkRead) {
                    firstBulkRead = false
                    return 0
                }
                return delegate.read(buffer, offset, length)
            }
        }

        assertArrayEquals(expected, HtmlPreviewerTextCodec.readBounded(input, expected.size))
    }

    @Test
    fun utfBomIsRemovedAndMalformedUtf8IsReplaced() {
        val utf16 = byteArrayOf(0xFF.toByte(), 0xFE.toByte()) +
            "Preview".toByteArray(StandardCharsets.UTF_16LE)
        assertEquals("Preview", HtmlPreviewerTextCodec.decode(utf16))
        assertEquals("\uFFFD", HtmlPreviewerTextCodec.decode(byteArrayOf(0xFF.toByte())))
    }
}

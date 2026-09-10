package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class HtmlPreviewerTextCodecTest {

    @Test
    fun boundedReaderAcceptsTheExactLimit() {
        val bytes = ByteArray(32) { it.toByte() }
        assertArrayEquals(bytes, HtmlPreviewerTextCodec.readBounded(ByteArrayInputStream(bytes), bytes.size))
    }

    @Test
    fun prefixReaderRetainsTheLimitAndReportsAdditionalContent() {
        val bytes = ByteArray(33) { it.toByte() }
        val result = HtmlPreviewerTextCodec.readPrefix(ByteArrayInputStream(bytes), 32)

        assertArrayEquals(bytes.copyOf(32), result.bytes)
        assertTrue(result.isTruncated)
    }

    @Test(expected = HtmlPreviewerTooLargeException::class)
    fun boundedReaderRejectsTheFirstByteOverTheLimit() {
        HtmlPreviewerTextCodec.readBounded(ByteArrayInputStream(ByteArray(33)), 32)
    }

    @Test
    fun boundedReaderMakesProgressAfterAZeroLengthRead() {
        val expected = "previewer".toByteArray()
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
            "Previewer".toByteArray(StandardCharsets.UTF_16LE)
        assertEquals("Previewer", HtmlPreviewerTextCodec.decode(utf16))
        assertEquals("\uFFFD", HtmlPreviewerTextCodec.decode(byteArrayOf(0xFF.toByte())))
    }

    @Test
    fun bomTakesPriorityOverAConflictingMetaDeclaration() {
        val utf8Html = "<meta charset=gbk><p>简体中文</p>"
        val utf8WithBom = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()) +
            utf8Html.toByteArray(StandardCharsets.UTF_8)
        assertEquals(utf8Html, HtmlPreviewerTextCodec.decode(utf8WithBom))

        val utf16Html = "<meta charset=windows-1252><p>Previewer</p>"
        val utf16WithBom = byteArrayOf(0xFF.toByte(), 0xFE.toByte()) +
            utf16Html.toByteArray(StandardCharsets.UTF_16LE)
        assertEquals(utf16Html, HtmlPreviewerTextCodec.decode(utf16WithBom))
    }

    @Test
    fun existingUtf32BomSupportIsPreserved() {
        val text = "UTF-32 previewer"
        val cases = listOf(
            "UTF-32BE" to byteArrayOf(0x00, 0x00, 0xFE.toByte(), 0xFF.toByte()),
            "UTF-32LE" to byteArrayOf(0xFF.toByte(), 0xFE.toByte(), 0x00, 0x00),
        )

        cases.forEach { (charsetName, bom) ->
            val encoded = bom + text.toByteArray(Charset.forName(charsetName))
            assertEquals(charsetName, text, HtmlPreviewerTextCodec.decode(encoded))
        }
    }

    @Test
    fun declaredLegacyEncodingMatrixDecodesWithoutABom() {
        val cases = listOf(
            EncodingCase("GBK", "<meta charset=gbk>", "简体中文"),
            EncodingCase("GB18030", "<meta charset=gb18030>", "扩展汉字龘"),
            EncodingCase("Big5", "<meta charset='big5-hkscs'>", "繁體中文"),
            EncodingCase("windows-31j", "<meta charset=x-sjis/>", "日本語"),
            EncodingCase(
                "EUC-KR",
                "<META content='text/html; charset=EUC-KR' HTTP-EQUIV='Content-Type'>",
                "한국어",
            ),
            EncodingCase("windows-1251", "<meta charset=windows-1251>", "Привет"),
            EncodingCase("windows-1250", "<meta charset=cp1250>", "Příliš"),
            EncodingCase("windows-1252", "<meta charset=iso-8859-1>", "café €"),
        )

        cases.forEach { case ->
            val html = "<html><head>${case.declaration}</head><body>${case.text}</body></html>"
            val decoded = HtmlPreviewerTextCodec.decode(html.toByteArray(Charset.forName(case.charset)))
            assertTrue("${case.charset} did not decode $decoded", decoded.contains(case.text))
        }
    }

    @Test
    fun prescanUsesTheFirstValidDeclarationAndIgnoresFalseCandidates() {
        val html = """
            <!-- <meta charset=utf-8> -->
            <meta content="text/html; charset=windows-1251">
            <meta charset=not-a-real-encoding>
            <meta charset=gb_2312>
            <meta charset=utf-8>
            <p>简体中文</p>
        """.trimIndent()

        val decoded = HtmlPreviewerTextCodec.decode(html.toByteArray(Charset.forName("GBK")))

        assertTrue(decoded.contains("简体中文"))
    }

    @Test
    fun missingLateOrProhibitedDeclarationsFallBackToUtf8() {
        val utf8Text = "<meta charset=utf-7><p>安全预览</p>"
        assertEquals(utf8Text, HtmlPreviewerTextCodec.decode(utf8Text.toByteArray()))

        val lateDeclaration = "<!--${"x".repeat(1024)}-->" +
            "<meta charset=windows-1251><p>Привет</p>"
        val decodedLateDeclaration = HtmlPreviewerTextCodec.decode(
            lateDeclaration.toByteArray(Charset.forName("windows-1251")),
        )
        assertFalse(decodedLateDeclaration.contains("Привет"))
    }

    private data class EncodingCase(
        val charset: String,
        val declaration: String,
        val text: String,
    )
}

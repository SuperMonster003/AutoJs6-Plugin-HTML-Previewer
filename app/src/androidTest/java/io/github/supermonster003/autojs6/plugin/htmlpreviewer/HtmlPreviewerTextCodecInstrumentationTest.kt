@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.Charset

@RunWith(AndroidJUnit4::class)
class HtmlPreviewerTextCodecInstrumentationTest {

    @Test
    fun requiredLegacyCharsetsDecodeOnTheAndroidRuntime() {
        (0..8).forEach { suffix ->
            val charsetName = "windows-125$suffix"
            assertTrue("$charsetName is unavailable", Charset.isSupported(charsetName))
        }
        val cases = listOf(
            EncodingCase("GBK", "gbk", "简体中文"),
            EncodingCase("Big5", "big5", "繁體中文"),
            EncodingCase("windows-31j", "shift_jis", "日本語"),
            EncodingCase("EUC-KR", "euc-kr", "한국어"),
            EncodingCase("windows-1251", "windows-1251", "Привет"),
            EncodingCase("windows-1252", "iso-8859-1", "café €"),
        )

        cases.forEach { case ->
            val html = "<meta charset=${case.label}><p>${case.text}</p>"
            val decoded = HtmlPreviewerTextCodec.decode(
                html.toByteArray(Charset.forName(case.charset)),
            )
            assertTrue("${case.charset} did not decode $decoded", decoded.contains(case.text))
        }

        val utf32Text = "UTF-32 previewer"
        listOf(
            "UTF-32BE" to byteArrayOf(0x00, 0x00, 0xFE.toByte(), 0xFF.toByte()),
            "UTF-32LE" to byteArrayOf(0xFF.toByte(), 0xFE.toByte(), 0x00, 0x00),
        ).forEach { (charsetName, bom) ->
            assertEquals(
                charsetName,
                utf32Text,
                HtmlPreviewerTextCodec.decode(
                    bom + utf32Text.toByteArray(Charset.forName(charsetName)),
                ),
            )
        }
    }

    private data class EncodingCase(
        val charset: String,
        val label: String,
        val text: String,
    )
}

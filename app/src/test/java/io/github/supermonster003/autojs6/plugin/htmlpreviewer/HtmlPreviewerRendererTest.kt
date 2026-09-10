package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlPreviewerRendererTest {

    private val renderer = HtmlPreviewerRenderer()

    @Test
    fun sanitizerRemovesExecutableContentAndKeepsSafeDocumentResources() {
        val html = """
            <!doctype html>
            <html>
            <head>
                <base href="https://evil.example/">
                <meta http-equiv="refresh" content="0; url=https://evil.example/">
                <link id="relative-css" rel="stylesheet" href="styles/site.css">
                <link id="remote-css" rel="stylesheet" href="https://evil.example/site.css">
                <style id="inline-style">body { color: #123456; }</style>
                <script id="script">alert(1)</script>
            </head>
            <body onload="alert(1)" style="padding: 1rem">
                <iframe id="frame" src="https://evil.example/"></iframe>
                <form action="https://evil.example/submit">
                    <button formaction="https://evil.example/other">Submit</button>
                </form>
                <img id="relative-image" src="images/picture.png" onerror="alert(1)">
                <img id="remote-image" src="https://example.com/picture.png">
                <img id="script-image" src="javascript:alert(1)">
                <a id="safe-link" href="https://example.com/docs" onclick="alert(1)">Docs</a>
                <a id="script-link" href="javascript:alert(1)">Unsafe</a>
            </body>
            </html>
        """.trimIndent()

        val document = Jsoup.parse(renderer.render(html))

        assertTrue(document.select("base, script, iframe, frame, object, embed, applet").isEmpty())
        assertFalse(document.body().hasAttr("onload"))
        assertEquals("padding: 1rem", document.body().attr("style"))
        assertNotNull(document.selectFirst("style#inline-style"))
        assertFalse(document.selectFirst("form")!!.hasAttr("action"))
        assertFalse(document.selectFirst("button")!!.hasAttr("formaction"))
        assertEquals("styles/site.css", document.selectFirst("#relative-css")?.attr("href"))
        assertFalse(document.selectFirst("#remote-css")!!.hasAttr("href"))
        assertEquals("images/picture.png", document.selectFirst("#relative-image")?.attr("src"))
        assertFalse(document.selectFirst("#relative-image")!!.hasAttr("onerror"))
        assertEquals("https://example.com/picture.png", document.selectFirst("#remote-image")?.attr("src"))
        assertEquals("lazy", document.selectFirst("#remote-image")?.attr("loading"))
        assertEquals("async", document.selectFirst("#remote-image")?.attr("decoding"))
        assertEquals("no-referrer", document.selectFirst("#remote-image")?.attr("referrerpolicy"))
        assertFalse(document.selectFirst("#script-image")!!.hasAttr("src"))
        assertEquals("https://example.com/docs", document.selectFirst("#safe-link")?.attr("href"))
        assertEquals("noopener noreferrer", document.selectFirst("#safe-link")?.attr("rel"))
        assertFalse(document.selectFirst("#safe-link")!!.hasAttr("onclick"))
        assertFalse(document.selectFirst("#script-link")!!.hasAttr("href"))
    }

    @Test
    fun trustedSecurityMetadataAndHtmlStylesheetAreInjected() {
        val document = Jsoup.parse(
            renderer.render(
                """
                    <html>
                    <head>
                        <meta http-equiv="Content-Security-Policy" content="default-src *">
                    </head>
                    <body><h1>Safe preview</h1></body>
                    </html>
                """.trimIndent(),
            ),
        )

        val cspElements = document.select("meta[http-equiv=Content-Security-Policy]")
        assertEquals(1, cspElements.size)
        val csp = cspElements.single().attr("content")
        assertTrue(csp.contains("script-src 'none'"))
        assertTrue(csp.contains("connect-src 'none'"))
        assertTrue(csp.contains("object-src 'none'"))
        assertTrue(csp.contains("base-uri 'none'"))
        assertTrue(csp.contains("form-action 'none'"))
        assertEquals(
            HtmlPreviewerSecurityPolicy.REFERRER_POLICY,
            document.selectFirst("meta[name=referrer]")?.attr("content"),
        )
        assertNotNull(
            document.selectFirst(
                """link[href="${HtmlPreviewerWebOrigin.PREVIEWER_ASSET_BASE_URL}html-base.css"]""",
            ),
        )
        assertTrue(document.selectFirst("html")?.hasClass("html-previewer-document") == true)
    }
}

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.jsoup.Jsoup
import org.jsoup.nodes.TextNode
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
                    <body><h1>Safe previewer</h1></body>
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

    @Test
    fun selectedThemeControlsInjectedColorSchemeMetadataAndStyle() {
        HtmlPreviewerThemeMode.entries.forEach { themeMode ->
            val document = Jsoup.parse(
                renderer.render("<p>Theme previewer</p>", themeMode),
            )

            assertEquals(
                themeMode.colorScheme,
                document.selectFirst("meta[name=color-scheme]")?.attr("content"),
            )
            assertTrue(
                document.selectFirst("style#html-previewer-theme")
                    ?.data()
                    ?.contains("color-scheme: ${themeMode.colorScheme} !important") == true,
            )
        }
    }

    @Test
    fun trustedTruncationNoticeIsAppendedAtTheEndOfTheSanitizedDocument() {
        val notice = "Rendered content ends here at 8 MB; remaining content was omitted"
        val document = Jsoup.parse(
            renderer.render(
                html = "<p>Visible prefix</p><script>removeMe()</script>",
                truncationNotice = notice,
            ),
        )

        assertTrue(document.select("script").isEmpty())
        val marker = document.selectFirst("body > aside#html-previewer-truncation-notice")
        assertEquals(notice, marker?.text())
        assertEquals("note", marker?.attr("role"))
        assertEquals(marker, document.body().children().last())
    }

    @Test
    fun networkImagePolicyChangesCspRemovesRemoteSourcesAndReportsTheirCount() {
        val html = """
            <html><body>
                <img id="remote-one" src="https://example.com/one.png">
                <img id="remote-two" src="HTTPS://images.example.org/two.webp">
                <img id="local" src="images/local.png">
                <img id="embedded" src="data:image/png;base64,iVBORw0KGgo=">
            </body></html>
        """.trimIndent()

        val enabledResult = renderer.renderResult(html, loadNetworkImages = true)
        val enabledDocument = Jsoup.parse(enabledResult.html)
        assertEquals(0, enabledResult.blockedNetworkResourceCount)
        assertEquals(
            "https://example.com/one.png",
            enabledDocument.selectFirst("#remote-one")?.attr("src"),
        )
        assertTrue(
            enabledDocument.selectFirst("meta[http-equiv=Content-Security-Policy]")
                ?.attr("content")
                ?.contains("img-src 'self' data: https:") == true,
        )

        val disabledResult = renderer.renderResult(html, loadNetworkImages = false)
        val disabledDocument = Jsoup.parse(disabledResult.html)
        assertEquals(2, disabledResult.blockedNetworkResourceCount)
        assertFalse(disabledDocument.selectFirst("#remote-one")!!.hasAttr("src"))
        assertFalse(disabledDocument.selectFirst("#remote-two")!!.hasAttr("src"))
        assertEquals("images/local.png", disabledDocument.selectFirst("#local")?.attr("src"))
        assertTrue(disabledDocument.selectFirst("#embedded")?.attr("src")?.startsWith("data:image/") == true)
        val disabledCsp = disabledDocument
            .selectFirst("meta[http-equiv=Content-Security-Policy]")
            ?.attr("content")
            .orEmpty()
        assertTrue(disabledCsp.contains("img-src 'self' data:;"))
        assertFalse(disabledCsp.contains("img-src 'self' data: https:"))
    }

    @Test
    fun alwaysRejectedNetworkResourceAddressesAreAlsoCounted() {
        val result = renderer.renderResult(
            """
                <html><head>
                    <link id="remote-style" rel="stylesheet" href="https://example.com/site.css">
                </head><body>
                    <img id="cleartext" src="http://example.com/image.png">
                    <img id="private" src="https://localhost/private.png">
                    <a id="external-link" href="https://example.com/docs">Docs</a>
                </body></html>
            """.trimIndent(),
            loadNetworkImages = true,
        )
        val document = Jsoup.parse(result.html)

        assertEquals(3, result.blockedNetworkResourceCount)
        assertFalse(document.selectFirst("#remote-style")!!.hasAttr("href"))
        assertFalse(document.selectFirst("#cleartext")!!.hasAttr("src"))
        assertFalse(document.selectFirst("#private")!!.hasAttr("src"))
        assertEquals("https://example.com/docs", document.selectFirst("#external-link")?.attr("href"))
    }

    @Test
    fun sourceModePreservesOriginalTextWithoutCreatingExecutableElements() {
        val source = """
            <!doctype html>
            <html>
	<head><title>Literal &amp; source</title></head>
	<body onload="steal()">
		<script id="source-script">window.evil = true</script>
		<img id="source-image" src="https://example.com/tracker.png">
		</code><iframe id="source-frame" src="https://example.com"></iframe>
	</body>
            </html>
        """.trimIndent()

        val result = renderer.renderResult(
            html = source,
            loadNetworkImages = true,
            viewMode = HtmlPreviewerViewMode.SOURCE,
        )
        val document = Jsoup.parse(result.html)
        val sourceCode = requireNotNull(document.selectFirst("pre#html-previewer-source > code"))

        assertEquals(0, result.blockedNetworkResourceCount)
        assertFalse(result.allowsNetworkImages)
        assertTrue(document.body().hasClass("html-previewer-source-view"))
        assertTrue(document.select("#source-script, #source-image, #source-frame").isEmpty())
        assertEquals(1, sourceCode.childNodeSize())
        assertTrue(sourceCode.childNode(0) is TextNode)
        assertEquals(source, (sourceCode.childNode(0) as TextNode).wholeText)
        val csp = document.selectFirst("meta[http-equiv=Content-Security-Policy]")
            ?.attr("content")
            .orEmpty()
        assertTrue(csp.contains("img-src 'self' data:;"))
        assertFalse(csp.contains("img-src 'self' data: https:"))
    }

    @Test
    fun sourceModeKeepsTheTrustedTruncationNoticeOutsideTheSourceText() {
        val source = "<p>Visible prefix</p>\n<!-- truncated source -->"
        val notice = "Rendered content ends here at 8 MB; remaining content was omitted"
        val document = Jsoup.parse(
            renderer.render(
                html = source,
                truncationNotice = notice,
                viewMode = HtmlPreviewerViewMode.SOURCE,
            ),
        )

        assertEquals(
            source,
            (document.selectFirst("#html-previewer-source > code")?.childNode(0) as TextNode).wholeText,
        )
        val marker = document.selectFirst("body > aside#html-previewer-truncation-notice")
        assertEquals(notice, marker?.text())
        assertEquals(marker, document.body().children().last())
    }

    @Test
    fun viewModeToggleAndSavedStateFallbackAreDeterministic() {
        assertEquals(
            HtmlPreviewerViewMode.SOURCE,
            HtmlPreviewerViewMode.RENDERED.toggled(),
        )
        assertEquals(
            HtmlPreviewerViewMode.RENDERED,
            HtmlPreviewerViewMode.SOURCE.toggled(),
        )
        assertEquals(
            HtmlPreviewerViewMode.SOURCE,
            HtmlPreviewerViewMode.fromSavedState(HtmlPreviewerViewMode.SOURCE.name),
        )
        assertEquals(
            HtmlPreviewerViewMode.RENDERED,
            HtmlPreviewerViewMode.fromSavedState("unknown"),
        )
    }

    @Test
    fun printTargetIsSanitizedLightAndUsesEagerImageLoading() {
        val result = renderer.renderResult(
            html = """
                <html><body style="color: red">
                <h1>Printable heading</h1>
                <script id="print-script">notPrintable()</script>
                <img id="local-print-image" src="images/local.png" loading="lazy">
                <img id="remote-print-image" src="https://example.com/remote.png">
                </body></html>
            """.trimIndent(),
            themeMode = HtmlPreviewerThemeMode.DARK,
            loadNetworkImages = true,
            viewMode = HtmlPreviewerViewMode.RENDERED,
            renderTarget = HtmlPreviewerRenderTarget.PRINT,
        )
        val document = Jsoup.parse(result.html)

        assertEquals(HtmlPreviewerViewMode.RENDERED, result.viewMode)
        assertEquals(HtmlPreviewerRenderTarget.PRINT, result.renderTarget)
        assertTrue(result.allowsNetworkImages)
        assertTrue(document.select("#print-script").isEmpty())
        assertTrue(document.selectFirst("html")?.hasClass("html-previewer-print-document") == true)
        assertEquals(
            HtmlPreviewerThemeMode.LIGHT.colorScheme,
            document.selectFirst("meta[name=color-scheme]")?.attr("content"),
        )
        listOf("#local-print-image", "#remote-print-image").forEach { selector ->
            val image = requireNotNull(document.selectFirst(selector))
            assertEquals("eager", image.attr("loading"))
            assertEquals("sync", image.attr("decoding"))
        }
        assertNotNull(document.selectFirst("style#html-previewer-print-theme"))
    }

    @Test
    fun printTargetRejectsSourceMode() {
        val error = runCatching {
            renderer.renderResult(
                html = "<script>literalSource()</script>",
                viewMode = HtmlPreviewerViewMode.SOURCE,
                renderTarget = HtmlPreviewerRenderTarget.PRINT,
            )
        }.exceptionOrNull()

        assertTrue(error is IllegalArgumentException)
    }
}

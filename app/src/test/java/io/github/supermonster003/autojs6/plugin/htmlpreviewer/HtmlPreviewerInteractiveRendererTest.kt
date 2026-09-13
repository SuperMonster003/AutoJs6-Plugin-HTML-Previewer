package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.jsoup.Jsoup
import org.junit.Assert.*
import org.junit.Test

class HtmlPreviewerInteractiveRendererTest {
    @org.junit.Test
    fun interactivePolicyBlocksScriptConnectionsWorkersAndSpeculativeLoads() {
        val result = HtmlPreviewerRenderer().renderResult(
            "<link rel='preconnect' href='https://blocked.invalid'><link rel='dns-prefetch' href='//blocked.invalid'><script>window.ok=true</script>",
            interactive = true,
        )
        val document = Jsoup.parse(result.html)
        val policy = document.selectFirst("meta[http-equiv=Content-Security-Policy]")!!.attr("content")
        assertTrue(policy.contains("connect-src 'none'"))
        assertTrue(policy.contains("webrtc 'block'"))
        assertTrue(policy.contains("worker-src 'none'"))
        assertTrue(policy.contains("frame-src 'none'"))
        assertTrue(document.select("link[rel=preconnect], link[rel=dns-prefetch]").isEmpty())
        assertTrue(document.select("script").isNotEmpty())
    }
    private val source = "<button onclick='startGame()'>Start</button>" +
        "<script src='https://cdn.example.com/three.js'></script><script>function startGame(){}</script>"

    @Test
    fun scriptsAndHandlersRequireInteractiveMode() {
        val safe = HtmlPreviewerRenderer().renderResult(source)
        assertFalse(safe.interactive)
        assertTrue(Jsoup.parse(safe.html).select("script,[onclick]").isEmpty())
        val interactive = HtmlPreviewerRenderer().renderResult(source, interactive = true)
        assertTrue(interactive.interactive)
        val document = Jsoup.parse(interactive.html)
        assertEquals(2, document.select("script").size)
        assertEquals("startGame()", document.selectFirst("button")!!.attr("onclick"))
        assertEquals("https://cdn.example.com/three.js", document.selectFirst("script[src]")!!.attr("src"))
        assertTrue(document.selectFirst("meta[http-equiv=Content-Security-Policy]")!!.attr("content").contains("script-src 'self' 'unsafe-inline' 'unsafe-eval' https:"))
    }

    @Test
    fun sourcePrintingAndDisabledNetworkStillEnforceTheirBoundaries() {
        for (result in listOf(
            HtmlPreviewerRenderer().renderResult(source, interactive = true, viewMode = HtmlPreviewerViewMode.SOURCE),
            HtmlPreviewerRenderer().renderResult(source, interactive = true, renderTarget = HtmlPreviewerRenderTarget.PRINT),
        )) {
            assertFalse(result.interactive)
            assertTrue(Jsoup.parse(result.html).select("script,[onclick]").isEmpty())
        }
        val offline = HtmlPreviewerRenderer().renderResult(source, interactive = true, loadNetworkImages = false)
        assertFalse(Jsoup.parse(offline.html).selectFirst("script")!!.hasAttr("src"))
        assertFalse(HtmlPreviewerSecurityPolicy.contentSecurityPolicy(false, true).contains("https:"))
        assertTrue(HtmlPreviewerSecurityPolicy.contentSecurityPolicy(true).contains("script-src 'none'"))
    }
}

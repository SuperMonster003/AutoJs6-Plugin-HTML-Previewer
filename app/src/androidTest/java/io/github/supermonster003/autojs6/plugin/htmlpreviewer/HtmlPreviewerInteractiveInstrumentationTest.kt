package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class HtmlPreviewerInteractiveInstrumentationTest {
    @Test
    fun interactiveCspBlocksFetchWebSocketAndWorkers() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val activity = instrumentation.startActivitySync(
            Intent(context, HtmlPreviewerWebViewTestActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        ) as HtmlPreviewerWebViewTestActivity
        var controller: HtmlPreviewerWebController? = null
        try {
            val ready = CountDownLatch(1)
            val rendered = HtmlPreviewerRenderer().renderResult("""
                <script>
                window.policyViolations=[];
                document.addEventListener('securitypolicyviolation', function(event) {
                    window.policyViolations.push(event.violatedDirective);
                });
                window.tryBlockedApis=function() {
                    fetch('https://csp-test.invalid/fetch').catch(function() {});
                    try { new WebSocket('wss://csp-test.invalid/socket'); } catch(error) {}
                    try { new Worker(URL.createObjectURL(new Blob(['postMessage(1)'], {type:'text/javascript'}))); } catch(error) {}
                };
                </script>
            """.trimIndent(), interactive = true, loadNetworkImages = true)
            instrumentation.runOnMainSync {
                controller = HtmlPreviewerWebController(activity, activity.webView,
                    resourceRoot = HtmlPreviewerTestContentProvider.parentUri(),
                    onExternalLink = {}, onPageFinished = { ready.countDown() })
                controller!!.show(rendered.html, loadNetworkImages = true, interactive = true)
            }
            assertTrue("CSP test page did not load", ready.await(20, TimeUnit.SECONDS))
            instrumentation.runOnMainSync { activity.webView.evaluateJavascript("window.tryBlockedApis()", null) }
            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10)
            var violations = ""
            while (System.nanoTime() < deadline) {
                val checked = CountDownLatch(1)
                instrumentation.runOnMainSync {
                    activity.webView.evaluateJavascript("JSON.stringify(window.policyViolations)") {
                        violations = it
                        checked.countDown()
                    }
                }
                assertTrue(checked.await(5, TimeUnit.SECONDS))
                if (violations.split("connect-src").size >= 3 && violations.contains("worker-src")) break
                Thread.sleep(100)
            }
            assertTrue("Fetch and WebSocket must both be rejected by CSP: $violations",
                violations.split("connect-src").size >= 3)
            assertTrue("Workers must be rejected by CSP: $violations", violations.contains("worker-src"))
        } finally {
            instrumentation.runOnMainSync { controller?.destroy(); activity.finish() }
        }
    }

    @Test
    fun interactivePagesLoadLocalScriptsRunWebGlAndHandleTheStartButton() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val script = HtmlPreviewerTestContentProvider.fileFor(context, "interaction-regression.js")
        script.writeText("window.localScriptLoaded=true; function startGame(){document.body.dataset.started='yes';}")
        val activity = instrumentation.startActivitySync(
            Intent(context, HtmlPreviewerWebViewTestActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        ) as HtmlPreviewerWebViewTestActivity
        var controller: HtmlPreviewerWebController? = null
        try {
            val pageReady = CountDownLatch(1)
            val source = """
                <button id="start" onclick="startGame()">Start game</button>
                <canvas id="scene"></canvas>
                <script src="interaction-regression.js"></script>
                <script>
                var canvas = document.getElementById('scene');
                window.webGlWorks = !!(canvas.getContext('webgl') || canvas.getContext('experimental-webgl'));
                </script>
            """.trimIndent()
            val result = HtmlPreviewerRenderer().renderResult(source, interactive = true, loadNetworkImages = false)
            instrumentation.runOnMainSync {
                controller = HtmlPreviewerWebController(
                    context = activity,
                    webView = activity.webView,
                    resourceRoot = HtmlPreviewerTestContentProvider.parentUri(),
                    onExternalLink = {},
                    onPageFinished = { pageReady.countDown() },
                )
                controller!!.show(result.html, loadNetworkImages = false, interactive = result.interactive)
            }
            assertTrue("Interactive page did not load", pageReady.await(15, TimeUnit.SECONDS))
            val evaluated = CountDownLatch(1)
            var resultJson: String? = null
            instrumentation.runOnMainSync {
                assertTrue(activity.webView.settings.javaScriptEnabled)
                assertTrue(activity.webView.settings.domStorageEnabled)
                assertFalse(activity.webView.settings.allowFileAccess)
                assertFalse(activity.webView.settings.allowContentAccess)
                activity.webView.evaluateJavascript(
                    "document.getElementById('start').click(); JSON.stringify([window.localScriptLoaded,window.webGlWorks,document.body.dataset.started]);",
                ) { value -> resultJson = value; evaluated.countDown() }
            }
            assertTrue(evaluated.await(10, TimeUnit.SECONDS))
            assertEquals("\"[true,true,\\\"yes\\\"]\"", resultJson)
            instrumentation.runOnMainSync {
                val safe = HtmlPreviewerRenderer().renderResult(source)
                controller!!.show(safe.html, interactive = safe.interactive)
                assertFalse(activity.webView.settings.javaScriptEnabled)
                assertFalse(activity.webView.settings.domStorageEnabled)
            }
        } finally {
            instrumentation.runOnMainSync { controller?.destroy(); activity.finish() }
            script.delete()
        }
    }
}

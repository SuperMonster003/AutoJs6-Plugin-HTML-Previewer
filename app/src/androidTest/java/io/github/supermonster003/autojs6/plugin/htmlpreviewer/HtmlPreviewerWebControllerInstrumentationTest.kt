@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.view.ContextThemeWrapper
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class HtmlPreviewerWebControllerInstrumentationTest {

    @Test
    fun documentResponseCspMatchesTheNetworkImagePolicy() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val handler = HtmlPreviewerDocumentPathHandler(
            contentResolver = context.contentResolver,
            rootUri = Uri.parse("content://html.previewer.test/root/document"),
        )

        handler.updateDocument("<p>Blocked</p>", loadNetworkImages = false)
        val blockedResponse = handler.handle(HtmlPreviewerWebOrigin.DOCUMENT_FILE_NAME)
        val blockedCsp = blockedResponse.responseHeaders["Content-Security-Policy"].orEmpty()
        blockedResponse.data.close()
        assertTrue(blockedCsp.contains("img-src 'self' data:;"))
        assertFalse(blockedCsp.contains("img-src 'self' data: https:"))

        handler.updateDocument("<p>Enabled</p>", loadNetworkImages = true)
        val enabledResponse = handler.handle(HtmlPreviewerWebOrigin.DOCUMENT_FILE_NAME)
        val enabledCsp = enabledResponse.responseHeaders["Content-Security-Policy"].orEmpty()
        enabledResponse.data.close()
        assertTrue(enabledCsp.contains("img-src 'self' data: https:;"))
    }

    @Test
    fun embeddedArchiveResourcesAreServedWithoutSiblingFallback() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val handler = HtmlPreviewerDocumentPathHandler(
            contentResolver = context.contentResolver,
            rootUri = Uri.parse("content://html.previewer.test/root/document"),
        )
        val path = "__mhtml__/resource-0001.png"
        val expected = byteArrayOf(1, 2, 3, 4)

        handler.updateDocument(
            html = "<img src=\"/$path\">",
            loadNetworkImages = false,
            embeddedResources = mapOf(
                path to HtmlPreviewerEmbeddedResource(expected, "image/png"),
            ),
            allowSiblingResources = false,
        )

        val resourceResponse = handler.handle(path)
        assertEquals(200, resourceResponse.statusCode)
        assertEquals("image/png", resourceResponse.mimeType)
        assertTrue(expected.contentEquals(resourceResponse.data.use { it.readBytes() }))
        val missingResponse = handler.handle("images/sibling.png")
        assertEquals(404, missingResponse.statusCode)
        missingResponse.data.close()
    }

    @Test
    fun generatedDocumentLoadsFromVirtualHttpsOrigin() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val targetContext = instrumentation.targetContext
        val resourceRoot = Uri.parse("content://html.previewer.test/root/document")

        val controller = AtomicReference<HtmlPreviewerWebController?>()
        val observedTitle = AtomicReference<String?>()
        val observedUrl = AtomicReference<String?>()
        val pageFinished = CountDownLatch(1)

        try {
            instrumentation.runOnMainSync {
                val context = ContextThemeWrapper(targetContext, R.style.AppTheme)
                val webView = WebView(context)
                controller.set(
                    HtmlPreviewerWebController(
                        context = context,
                        webView = webView,
                        resourceRoot = resourceRoot,
                        onExternalLink = {},
                        onPageFinished = {
                            observedTitle.set(webView.title)
                            observedUrl.set(webView.url)
                            pageFinished.countDown()
                        },
                    ).also {
                        val renderedPreviewer = HtmlPreviewerRenderer().render(
                            """
                                <!doctype html>
                                <html>
                                <head><title>$EXPECTED_DOCUMENT_TITLE</title></head>
                                <body><h1>HTML Previewer loaded</h1></body>
                                </html>
                            """.trimIndent(),
                        )
                        it.show(renderedPreviewer)
                    },
                )
            }

            assertTrue(
                "Timed out waiting for the generated previewer document",
                pageFinished.await(PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            instrumentation.waitForIdleSync()
            assertEquals(EXPECTED_DOCUMENT_TITLE, observedTitle.get())
            assertEquals(HtmlPreviewerWebOrigin.DOCUMENT_URL, observedUrl.get())
        } finally {
            instrumentation.runOnMainSync {
                controller.getAndSet(null)?.destroy()
            }
        }
    }

    @Test
    fun findInPageReportsCountAndNavigatesMatches() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val targetContext = instrumentation.targetContext
        val resourceRoot = Uri.parse("content://html.previewer.test/root/document")

        val activity = AtomicReference<HtmlPreviewerWebViewTestActivity?>()
        val controller = AtomicReference<HtmlPreviewerWebController?>()
        val findResults = LinkedBlockingQueue<HtmlPreviewerFindResult>()
        val pageFinished = CountDownLatch(1)
        val renderedPreviewer = HtmlPreviewerRenderer().render(largeSearchableDocument())

        try {
            activity.set(
                instrumentation.startActivitySync(
                    Intent(targetContext, HtmlPreviewerWebViewTestActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ) as HtmlPreviewerWebViewTestActivity,
            )
            instrumentation.runOnMainSync {
                val testActivity = requireNotNull(activity.get())
                val webView = testActivity.webView.apply { requestFocus() }
                controller.set(
                    HtmlPreviewerWebController(
                        context = testActivity,
                        webView = webView,
                        resourceRoot = resourceRoot,
                        onExternalLink = {},
                        onPageFinished = pageFinished::countDown,
                        onFindResult = { result ->
                            if (result.isDoneCounting) {
                                findResults.offer(result)
                            }
                        },
                    ).also {
                        it.show(renderedPreviewer)
                    },
                )
            }

            assertTrue(
                "Timed out waiting for the searchable previewer document",
                pageFinished.await(PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            instrumentation.runOnMainSync {
                controller.get()?.setTextZoom(175)
                assertEquals(175, requireNotNull(activity.get()).webView.settings.textZoom)
                controller.get()?.findAll(FIND_QUERY)
            }
            val initialResult = findResults.poll(FIND_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            assertTrue(
                "Timed out waiting for find-in-page results",
                initialResult != null,
            )
            assertEquals(3, initialResult?.numberOfMatches)
            assertEquals(0, initialResult?.activeMatchOrdinal)

            instrumentation.runOnMainSync {
                controller.get()?.findNext(forward = true)
            }
            val nextResult = findResults.poll(FIND_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            assertTrue("Timed out waiting for the next match", nextResult != null)
            assertEquals(3, nextResult?.numberOfMatches)
            assertEquals(1, nextResult?.activeMatchOrdinal)

            instrumentation.runOnMainSync {
                controller.get()?.findNext(forward = false)
            }
            val previousResult = findResults.poll(FIND_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            assertTrue("Timed out waiting for the previous match", previousResult != null)
            assertEquals(3, previousResult?.numberOfMatches)
            assertEquals(0, previousResult?.activeMatchOrdinal)
        } finally {
            instrumentation.runOnMainSync {
                controller.getAndSet(null)?.destroy()
                activity.getAndSet(null)?.finish()
            }
        }
    }

    @Test
    fun sourceDocumentIsSearchableAndKeepsNetworkLoadsBlocked() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val targetContext = instrumentation.targetContext
        val resourceRoot = Uri.parse("content://html.previewer.test/root/document")
        val activity = AtomicReference<HtmlPreviewerWebViewTestActivity?>()
        val controller = AtomicReference<HtmlPreviewerWebController?>()
        val findResults = LinkedBlockingQueue<HtmlPreviewerFindResult>()
        val pageFinished = CountDownLatch(1)
        val sourceResult = HtmlPreviewerRenderer().renderResult(
            html = "<script>$SOURCE_FIND_QUERY</script><img src=\"https://example.com/source.png\">",
            loadNetworkImages = true,
            viewMode = HtmlPreviewerViewMode.SOURCE,
        )

        try {
            activity.set(
                instrumentation.startActivitySync(
                    Intent(targetContext, HtmlPreviewerWebViewTestActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ) as HtmlPreviewerWebViewTestActivity,
            )
            instrumentation.runOnMainSync {
                val testActivity = requireNotNull(activity.get())
                controller.set(
                    HtmlPreviewerWebController(
                        context = testActivity,
                        webView = testActivity.webView,
                        resourceRoot = resourceRoot,
                        onExternalLink = {},
                        onPageFinished = pageFinished::countDown,
                        onFindResult = { result ->
                            if (result.isDoneCounting) {
                                findResults.offer(result)
                            }
                        },
                    ).also {
                        it.show(
                            html = sourceResult.html,
                            loadNetworkImages = sourceResult.allowsNetworkImages,
                        )
                    },
                )
            }

            assertTrue(
                "Timed out waiting for the source document",
                pageFinished.await(PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            instrumentation.runOnMainSync {
                requireNotNull(activity.get()).webView.settings.apply {
                    assertFalse(blockNetworkImage)
                    assertTrue(blockNetworkLoads)
                }
                controller.get()?.findAll(SOURCE_FIND_QUERY)
            }
            val findResult = findResults.poll(FIND_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            assertTrue("Timed out finding source text", findResult != null)
            assertEquals(1, findResult?.numberOfMatches)
        } finally {
            instrumentation.runOnMainSync {
                controller.getAndSet(null)?.destroy()
                activity.getAndSet(null)?.finish()
            }
        }
    }

    @Test
    fun forcedThemeModesChangeTheRenderedBackground() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val targetContext = instrumentation.targetContext
        val resourceRoot = Uri.parse("content://html.previewer.test/root/document")

        val activity = AtomicReference<HtmlPreviewerWebViewTestActivity?>()
        val controller = AtomicReference<HtmlPreviewerWebController?>()
        val pageFinished = LinkedBlockingQueue<Boolean>()

        try {
            activity.set(
                instrumentation.startActivitySync(
                    Intent(targetContext, HtmlPreviewerWebViewTestActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ) as HtmlPreviewerWebViewTestActivity,
            )
            instrumentation.runOnMainSync {
                val testActivity = requireNotNull(activity.get())
                controller.set(
                    HtmlPreviewerWebController(
                        context = testActivity,
                        webView = testActivity.webView,
                        resourceRoot = resourceRoot,
                        onExternalLink = {},
                        onPageFinished = { pageFinished.offer(true) },
                    ),
                )
            }

            fun renderAndCaptureBackground(themeMode: HtmlPreviewerThemeMode): Int {
                instrumentation.runOnMainSync {
                    controller.get()?.apply {
                        setThemeMode(themeMode)
                        show(
                            HtmlPreviewerRenderer().render(
                                "<html><body><p>Theme background</p></body></html>",
                                themeMode,
                            ),
                        )
                    }
                }
                assertTrue(
                    "Timed out waiting for the $themeMode previewer",
                    pageFinished.poll(PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS) == true,
                )
                instrumentation.waitForIdleSync()

                val observedColor = AtomicReference<Int>()
                instrumentation.runOnMainSync {
                    val webView = requireNotNull(activity.get()).webView
                    val bitmap = Bitmap.createBitmap(32, 32, Bitmap.Config.ARGB_8888)
                    try {
                        webView.draw(Canvas(bitmap))
                        observedColor.set(bitmap.getPixel(1, 1))
                    } finally {
                        bitmap.recycle()
                    }
                }
                return requireNotNull(observedColor.get())
            }

            val darkBackground = renderAndCaptureBackground(HtmlPreviewerThemeMode.DARK)
            val lightBackground = renderAndCaptureBackground(HtmlPreviewerThemeMode.LIGHT)

            assertTrue(
                "Expected dark mode ($darkBackground) to be darker than light mode ($lightBackground)",
                Color.luminance(darkBackground) < Color.luminance(lightBackground),
            )
        } finally {
            instrumentation.runOnMainSync {
                controller.getAndSet(null)?.destroy()
                activity.getAndSet(null)?.finish()
            }
        }
    }

    @Test
    fun untrustedDocumentCapabilitiesAreDisabled() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val targetContext = instrumentation.targetContext

        instrumentation.runOnMainSync {
            val context = ContextThemeWrapper(targetContext, R.style.AppTheme)
            val webView = WebView(context)
            val controller = HtmlPreviewerWebController(
                context = context,
                webView = webView,
                resourceRoot = Uri.parse("content://html.previewer.test/root/document"),
                onExternalLink = {},
            )
            try {
                controller.setTextZoom(175)
                webView.settings.apply {
                    assertFalse(javaScriptEnabled)
                    assertFalse(javaScriptCanOpenWindowsAutomatically)
                    assertFalse(domStorageEnabled)
                    assertFalse(databaseEnabled)
                    assertFalse(allowFileAccess)
                    assertFalse(allowContentAccess)
                    assertFalse(allowFileAccessFromFileURLs)
                    assertFalse(allowUniversalAccessFromFileURLs)
                    assertFalse(blockNetworkImage)
                    assertFalse(blockNetworkLoads)
                    assertTrue(builtInZoomControls)
                    assertFalse(displayZoomControls)
                    assertEquals(175, textZoom)
                    assertEquals(WebSettings.MIXED_CONTENT_NEVER_ALLOW, mixedContentMode)
                }
            } finally {
                controller.destroy()
            }
        }
    }

    @Test
    fun networkBlockingStillLoadsVirtualDocumentsAndCanBeReenabled() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val targetContext = instrumentation.targetContext
        val resourceRoot = Uri.parse("content://html.previewer.test/root/document")
        val activity = AtomicReference<HtmlPreviewerWebViewTestActivity?>()
        val controller = AtomicReference<HtmlPreviewerWebController?>()
        val finishedTitles = LinkedBlockingQueue<String>()
        val blockedCounts = LinkedBlockingQueue<Int>()
        val blockedResult = HtmlPreviewerRenderer().renderResult(
            """
                <html>
                <head><title>$NETWORK_BLOCKED_TITLE</title></head>
                <body>
                    <img src="https://example.com/one.png">
                    <img src="https://images.example.org/two.webp">
                </body>
                </html>
            """.trimIndent(),
            loadNetworkImages = false,
        )

        try {
            activity.set(
                instrumentation.startActivitySync(
                    Intent(targetContext, HtmlPreviewerWebViewTestActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ) as HtmlPreviewerWebViewTestActivity,
            )
            instrumentation.runOnMainSync {
                val testActivity = requireNotNull(activity.get())
                val webView = testActivity.webView
                controller.set(
                    HtmlPreviewerWebController(
                        context = testActivity,
                        webView = webView,
                        resourceRoot = resourceRoot,
                        onExternalLink = {},
                        onPageFinished = { finishedTitles.offer(webView.title.orEmpty()) },
                        onBlockedResourceCountChanged = blockedCounts::offer,
                    ).also {
                        it.show(
                            html = blockedResult.html,
                            loadNetworkImages = false,
                            initiallyBlockedResourceCount = blockedResult.blockedNetworkResourceCount,
                        )
                    },
                )
            }

            assertEquals(
                NETWORK_BLOCKED_TITLE,
                finishedTitles.poll(PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            assertEquals(2, blockedCounts.poll(PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS))
            instrumentation.runOnMainSync {
                requireNotNull(activity.get()).webView.settings.apply {
                    assertFalse(blockNetworkImage)
                    assertTrue(blockNetworkLoads)
                }
                controller.get()?.show(
                    html = HtmlPreviewerRenderer().render(
                        "<html><head><title>$NETWORK_ENABLED_TITLE</title></head><body>Local only</body></html>",
                    ),
                    loadNetworkImages = true,
                )
            }

            assertEquals(
                NETWORK_ENABLED_TITLE,
                finishedTitles.poll(PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            assertEquals(0, blockedCounts.poll(PAGE_LOAD_TIMEOUT_SECONDS, TimeUnit.SECONDS))
            instrumentation.runOnMainSync {
                requireNotNull(activity.get()).webView.settings.apply {
                    assertFalse(blockNetworkImage)
                    assertFalse(blockNetworkLoads)
                }
            }
        } finally {
            instrumentation.runOnMainSync {
                controller.getAndSet(null)?.destroy()
                activity.getAndSet(null)?.finish()
            }
        }
    }

    companion object {
        private const val EXPECTED_DOCUMENT_TITLE = "html-previewer-load-ok"
        private const val FIND_QUERY = "html-previewer-find-target"
        private const val FIND_TIMEOUT_SECONDS = 10L
        private const val LARGE_DOCUMENT_PARAGRAPHS = 1024
        private const val PAGE_LOAD_TIMEOUT_SECONDS = 20L
        private const val SOURCE_FIND_QUERY = "html-previewer-source-find-target"
        private const val NETWORK_BLOCKED_TITLE = "html-previewer-network-blocked"
        private const val NETWORK_ENABLED_TITLE = "html-previewer-network-enabled"

        private val LARGE_DOCUMENT_FILLER = "x".repeat(1024)

        private fun largeSearchableDocument(): String = buildString {
            append("<!doctype html><html><body>")
            append("<p>$FIND_QUERY first</p>")
            repeat(LARGE_DOCUMENT_PARAGRAPHS / 2) {
                append("<p>").append(LARGE_DOCUMENT_FILLER).append("</p>")
            }
            append("<p>second $FIND_QUERY</p>")
            repeat(LARGE_DOCUMENT_PARAGRAPHS / 2) {
                append("<p>").append(LARGE_DOCUMENT_FILLER).append("</p>")
            }
            append("<p>$FIND_QUERY third</p>")
            append("</body></html>")
        }
    }
}

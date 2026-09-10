@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.app.Instrumentation
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.SystemClock
import android.util.Base64
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.TextView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.google.android.material.appbar.MaterialToolbar
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class HtmlPreviewerMhtmlInstrumentationTest {

    @Test
    fun archiveCssAndImageRenderWhileScriptsStayLiteralOnlyInSourceMode() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val preferences = HtmlPreviewerPreferences(context)
        val sharedPreferences = context.getSharedPreferences(
            HtmlPreviewerPreferences.PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )
        val hadStoredNetworkPreference = sharedPreferences.contains(
            HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES,
        )
        val originalNetworkPreference = preferences.loadNetworkImages
        val archiveFile = HtmlPreviewerTestContentProvider.fileFor(context, TEST_FILE_NAME)
        var activity: HtmlPreviewerActivity? = null

        try {
            archiveFile.writeText(mhtmlArchive(redPngBase64()), Charsets.ISO_8859_1)
            assertTrue(
                sharedPreferences.edit()
                    .putBoolean(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES, false)
                    .commit(),
            )
            activity = instrumentation.startActivitySync(
                previewerIntent(archiveFile)
                    .setClass(context, HtmlPreviewerActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            ) as HtmlPreviewerActivity
            val viewer = requireNotNull(activity)
            assertViewerLoaded(instrumentation, viewer, "MHTML Previewer")

            assertArchiveColors(instrumentation, viewer)
            assertEquals(1, findCount(instrumentation, viewer, PREVIEWER_MARKER))
            assertEquals(0, findCount(instrumentation, viewer, SOURCE_ONLY_MARKER))
            instrumentation.runOnMainSync {
                val banner = viewer.findViewById<TextView>(R.id.blocked_resources_banner)
                assertTrue(banner.isShown)
                assertEquals(
                    viewer.getString(R.string.text_blocked_resource_count, 1),
                    banner.text.toString(),
                )
            }
            instrumentation.runOnMainSync {
                assertTrue(viewer.onOptionsItemSelected(sourceToggleItem(viewer)))
            }
            assertViewerLoaded(instrumentation, viewer, "MHTML source")
            assertEquals(1, findCount(instrumentation, viewer, SOURCE_ONLY_MARKER))
            assertEquals(1, findCount(instrumentation, viewer, "cid:archive-image"))
            instrumentation.runOnMainSync {
                viewer.findViewById<WebView>(R.id.previewer_web_view).settings.apply {
                    assertFalse(blockNetworkImage)
                    assertTrue(blockNetworkLoads)
                }
                assertFalse(viewer.findViewById<View>(R.id.blocked_resources_banner).isShown)
                assertEquals(
                    viewer.getString(R.string.text_view_previewer),
                    sourceToggleItem(viewer).title.toString(),
                )
            }

            instrumentation.runOnMainSync {
                val refresh = viewer.findViewById<MaterialToolbar>(R.id.toolbar)
                    .menu
                    .findItem(R.id.action_refresh)
                assertTrue(viewer.onOptionsItemSelected(refresh))
            }
            assertViewerLoaded(instrumentation, viewer, "refreshed MHTML source")
            assertEquals(1, findCount(instrumentation, viewer, SOURCE_ONLY_MARKER))

            instrumentation.runOnMainSync {
                assertTrue(viewer.onOptionsItemSelected(sourceToggleItem(viewer)))
            }
            assertViewerLoaded(instrumentation, viewer, "restored MHTML Previewer")
            assertArchiveColors(instrumentation, viewer)
        } finally {
            activity?.let { viewer -> instrumentation.runOnMainSync { viewer.finish() } }
            sharedPreferences.edit().apply {
                if (hadStoredNetworkPreference) {
                    putBoolean(
                        HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES,
                        originalNetworkPreference,
                    )
                } else {
                    remove(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES)
                }
            }.commit()
            archiveFile.delete()
        }
    }

    @Test
    fun oversizedArchiveIsRejectedWithoutOfferingAnInvalidTruncatedPreviewer() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val archiveFile = HtmlPreviewerTestContentProvider.fileFor(context, OVERSIZED_TEST_FILE_NAME)
        var activity: HtmlPreviewerActivity? = null

        try {
            writeOversizedArchive(archiveFile)
            activity = instrumentation.startActivitySync(
                previewerIntent(archiveFile)
                    .setClass(context, HtmlPreviewerActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            ) as HtmlPreviewerActivity
            val viewer = requireNotNull(activity)

            assertTrue(
                "Timed out waiting for the oversized MHTML error",
                waitUntil(instrumentation, UI_TIMEOUT_SECONDS) {
                    viewer.findViewById<View>(R.id.error_panel).isShown &&
                        !viewer.findViewById<View>(R.id.loading_indicator).isShown
                },
            )
            instrumentation.runOnMainSync {
                assertFalse(viewer.findViewById<View>(R.id.previewer_truncated_button).isShown)
                assertFalse(viewer.findViewById<WebView>(R.id.previewer_web_view).isShown)
            }
        } finally {
            activity?.let { viewer -> instrumentation.runOnMainSync { viewer.finish() } }
            archiveFile.delete()
        }
    }

    private fun assertArchiveColors(
        instrumentation: Instrumentation,
        viewer: HtmlPreviewerActivity,
    ) {
        var lastImagePixel = Color.TRANSPARENT
        var lastBackgroundPixel = Color.TRANSPARENT
        val rendered = waitUntil(instrumentation, UI_TIMEOUT_SECONDS) {
            val webView = viewer.findViewById<WebView>(R.id.previewer_web_view)
            if (webView.width < 160 || webView.height < 160) return@waitUntil false
            val bitmap = Bitmap.createBitmap(webView.width, webView.height, Bitmap.Config.ARGB_8888)
            try {
                webView.draw(Canvas(bitmap))
                lastImagePixel = bitmap.getPixel(24, 24)
                lastBackgroundPixel = bitmap.getPixel(webView.width - 24, webView.height - 24)
                isMostlyRed(lastImagePixel) && isArchiveBackground(lastBackgroundPixel)
            } finally {
                bitmap.recycle()
            }
        }
        assertTrue(
            "Timed out waiting for embedded MHTML image and CSS pixels; " +
                "image=${Integer.toHexString(lastImagePixel)}, " +
                "background=${Integer.toHexString(lastBackgroundPixel)}",
            rendered,
        )
    }

    private fun isMostlyRed(color: Int): Boolean =
        Color.red(color) >= 180 && Color.green(color) <= 80 && Color.blue(color) <= 80

    private fun isArchiveBackground(color: Int): Boolean =
        Color.red(color) in 0..48 && Color.green(color) in 8..72 && Color.blue(color) >= 72

    private fun assertViewerLoaded(
        instrumentation: Instrumentation,
        viewer: HtmlPreviewerActivity,
        description: String,
    ) {
        assertTrue(
            "Timed out waiting for $description",
            waitUntil(instrumentation, UI_TIMEOUT_SECONDS) {
                viewer.findViewById<WebView>(R.id.previewer_web_view).isShown &&
                    !viewer.findViewById<View>(R.id.loading_indicator).isShown
            },
        )
    }

    private fun sourceToggleItem(viewer: HtmlPreviewerActivity): MenuItem =
        requireNotNull(
            viewer.findViewById<MaterialToolbar>(R.id.toolbar)
                .menu
                .findItem(R.id.action_toggle_source),
        )

    private fun findCount(
        instrumentation: Instrumentation,
        viewer: HtmlPreviewerActivity,
        query: String,
    ): Int {
        val result = CountDownLatch(1)
        val matchCount = AtomicInteger(-1)
        instrumentation.runOnMainSync {
            viewer.findViewById<WebView>(R.id.previewer_web_view).apply {
                setFindListener { _, numberOfMatches, isDoneCounting ->
                    if (isDoneCounting) {
                        matchCount.set(numberOfMatches)
                        result.countDown()
                    }
                }
                clearMatches()
                findAllAsync(query)
            }
        }
        assertTrue(
            "Timed out finding $query",
            result.await(FIND_TIMEOUT_SECONDS, TimeUnit.SECONDS),
        )
        instrumentation.runOnMainSync {
            viewer.findViewById<WebView>(R.id.previewer_web_view).apply {
                setFindListener(null)
                clearMatches()
            }
        }
        return matchCount.get()
    }

    private fun previewerIntent(documentFile: File): Intent {
        val documentUri = HtmlPreviewerTestContentProvider.documentUri(documentFile.name)
        val parentUri = HtmlPreviewerTestContentProvider.parentUri()
        val clipData = ClipData(
            ClipDescription("MHTML Previewer", arrayOf(MHTML_MIME_TYPE)),
            ClipData.Item(documentUri),
        ).apply { addItem(ClipData.Item(parentUri)) }
        return Intent(ExplorerActionPluginActions.EXECUTE)
            .setDataAndType(documentUri, MHTML_MIME_TYPE)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            .putExtra(ExplorerActionIntentExtras.ACTION_ID, HtmlPreviewerPlugin.ID)
            .putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, HtmlPreviewerPlugin.PROTOCOL_VERSION)
            .putExtra(
                ExplorerActionIntentExtras.HOST_VERSION_CODE,
                HtmlPreviewerExplorerCompatibility.maximumAuditedHostVersionCode,
            )
            .putExtra(ExplorerActionIntentExtras.DISPLAY_NAME, documentFile.name)
            .putExtra(ExplorerActionIntentExtras.SIZE, documentFile.length())
            .putExtra(ExplorerActionIntentExtras.PARENT_URI, parentUri)
            .putExtra(
                ExplorerActionIntentExtras.SOURCE_SURFACE,
                ExplorerActionIntentValues.SOURCE_SURFACE_MAIN,
            )
            .apply { this.clipData = clipData }
    }

    private fun redPngBase64(): String {
        val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).apply {
            eraseColor(Color.RED)
        }
        return try {
            val output = ByteArrayOutputStream()
            assertTrue(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
            Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP)
        } finally {
            bitmap.recycle()
        }
    }

    private fun writeOversizedArchive(file: File) {
        val header = """
            MIME-Version: 1.0
            Content-Type: multipart/related; boundary="oversized"

            --oversized
            Content-Type: text/html

        """.trimIndent().replace("\n", "\r\n").toByteArray(Charsets.US_ASCII)
        file.outputStream().buffered().use { output ->
            output.write(header)
            var remaining = HtmlPreviewerActivity.MAX_HTML_BYTES + 1 - header.size
            val chunk = ByteArray(16 * 1024) { 'x'.code.toByte() }
            while (remaining > 0) {
                val count = minOf(remaining, chunk.size)
                output.write(chunk, 0, count)
                remaining -= count
            }
        }
        check(file.length() > HtmlPreviewerActivity.MAX_HTML_BYTES)
    }

    private fun mhtmlArchive(imageBase64: String): String = """
        MIME-Version: 1.0
        Content-Type: multipart/related; boundary="$BOUNDARY"; type="text/html"; start="<archive-root>"

        --$BOUNDARY
        Content-Type: text/css; charset=utf-8
        Content-ID: <archive-style>
        Content-Location: https://archive.example/assets/style.css

        body { margin: 0; padding: 0; background: #102060 !important; color: white; }
        #archive-image { display: block; width: 96px; height: 96px; max-width: none; }
        --$BOUNDARY
        Content-Type: image/png
        Content-Transfer-Encoding: base64
        Content-ID: <archive-image>
        Content-Location: https://archive.example/assets/image.png

        $imageBase64
        --$BOUNDARY
        Content-Type: text/html; charset=utf-8
        Content-ID: <archive-root>
        Content-Location: https://archive.example/pages/index.html

        <!doctype html><html><head>
        <link rel="stylesheet" href="cid:archive-style">
        </head><body>
        <img id="archive-image" src="cid:archive-image">
        <p>$PREVIEWER_MARKER</p>
        <script>$SOURCE_ONLY_MARKER</script>
        <img src="https://example.com/missing-from-archive.png">
        </body></html>
        --$BOUNDARY--
    """.trimIndent().replace("\n", "\r\n")

    private fun waitUntil(
        instrumentation: Instrumentation,
        timeoutSeconds: Long,
        condition: () -> Boolean,
    ): Boolean {
        val deadline = SystemClock.elapsedRealtime() + TimeUnit.SECONDS.toMillis(timeoutSeconds)
        while (SystemClock.elapsedRealtime() < deadline) {
            val satisfied = AtomicBoolean()
            instrumentation.runOnMainSync { satisfied.set(condition()) }
            if (satisfied.get()) return true
            SystemClock.sleep(POLL_INTERVAL_MILLIS)
        }
        return false
    }

    companion object {
        private const val BOUNDARY = "html-previewer-mhtml-instrumentation"
        private const val FIND_TIMEOUT_SECONDS = 10L
        private const val MHTML_MIME_TYPE = "multipart/related"
        private const val OVERSIZED_TEST_FILE_NAME = "oversized-archive.mhtml"
        private const val POLL_INTERVAL_MILLIS = 75L
        private const val PREVIEWER_MARKER = "mhtml-rendered-previewer-marker"
        private const val SOURCE_ONLY_MARKER = "mhtml-source-only-marker"
        private const val TEST_FILE_NAME = "archive-previewer.mhtml"
        private const val UI_TIMEOUT_SECONDS = 30L
    }
}

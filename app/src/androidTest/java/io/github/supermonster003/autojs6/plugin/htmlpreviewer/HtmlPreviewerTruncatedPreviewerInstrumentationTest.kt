@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.SystemClock
import android.view.View
import android.webkit.WebView
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class HtmlPreviewerTruncatedPreviewerInstrumentationTest {

    @Test
    fun prefixReaderRetainsTheBoundaryAndReportsTruncation() {
        val bytes = ByteArray(33) { it.toByte() }
        val prefix = HtmlPreviewerTextCodec.readPrefix(ByteArrayInputStream(bytes), 32)

        assertTrue(prefix.isTruncated)
        assertEquals(32, prefix.bytes.size)
        assertFalse(HtmlPreviewerTextCodec.readPrefix(ByteArrayInputStream(ByteArray(32)), 32).isTruncated)
    }

    @Test
    fun oversizedViewerOffersAndLoadsAnExplicitTruncatedPreviewer() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val documentFile = HtmlPreviewerTestContentProvider.fileFor(context, OVERSIZED_FILE_NAME)
        var activity: HtmlPreviewerActivity? = null

        try {
            writeOversizedHtml(documentFile)
            val viewer = (instrumentation.startActivitySync(
                previewerIntent()
                    .setClass(context, HtmlPreviewerActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            ) as HtmlPreviewerActivity).also { activity = it }
            assertTrue(
                "Timed out waiting for the oversized-file choice",
                waitUntil(instrumentation, UI_TIMEOUT_SECONDS) {
                    viewer.findViewById<View>(R.id.error_panel).isShown &&
                        viewer.findViewById<View>(R.id.previewer_truncated_button).isShown
                },
            )

            instrumentation.runOnMainSync {
                viewer.findViewById<View>(R.id.previewer_truncated_button).performClick()
            }

            assertTrue(
                "Timed out waiting for the truncated previewer",
                waitUntil(instrumentation, UI_TIMEOUT_SECONDS) {
                    viewer.findViewById<View>(R.id.truncation_banner).isShown &&
                        viewer.findViewById<WebView>(R.id.previewer_web_view).isShown &&
                        !viewer.findViewById<View>(R.id.loading_indicator).isShown
                },
            )

            val findResult = CountDownLatch(1)
            val matchCount = AtomicInteger()
            instrumentation.runOnMainSync {
                viewer.findViewById<WebView>(R.id.previewer_web_view).apply {
                    setFindListener { _, numberOfMatches, isDoneCounting ->
                        if (isDoneCounting) {
                            matchCount.set(numberOfMatches)
                            findResult.countDown()
                        }
                    }
                    findAllAsync(PREFIX_TARGET)
                }
            }
            assertTrue(
                "Timed out finding content in the truncated prefix",
                findResult.await(FIND_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            assertEquals(1, matchCount.get())
        } finally {
            activity?.let { viewer ->
                instrumentation.runOnMainSync { viewer.finish() }
            }
            documentFile.delete()
        }
    }

    private fun previewerIntent(): Intent {
        val documentUri = HtmlPreviewerTestContentProvider.documentUri(OVERSIZED_FILE_NAME)
        val parentUri = HtmlPreviewerTestContentProvider.parentUri()
        val clipData = ClipData(
            ClipDescription("Oversized HTML Previewer", arrayOf("text/html")),
            ClipData.Item(documentUri),
        ).apply {
            addItem(ClipData.Item(parentUri))
        }
        return Intent(ExplorerActionPluginActions.EXECUTE)
            .setDataAndType(documentUri, "text/html")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            .putExtra(ExplorerActionIntentExtras.ACTION_ID, HtmlPreviewerPlugin.ID)
            .putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, HtmlPreviewerPlugin.PROTOCOL_VERSION)
            .putExtra(
                ExplorerActionIntentExtras.HOST_VERSION_CODE,
                HtmlPreviewerExplorerCompatibility.maximumAuditedHostVersionCode,
            )
            .putExtra(ExplorerActionIntentExtras.DISPLAY_NAME, OVERSIZED_FILE_NAME)
            .putExtra(
                ExplorerActionIntentExtras.SIZE,
                HtmlPreviewerActivity.MAX_HTML_BYTES.toLong() + 1L,
            )
            .putExtra(ExplorerActionIntentExtras.PARENT_URI, parentUri)
            .putExtra(
                ExplorerActionIntentExtras.SOURCE_SURFACE,
                ExplorerActionIntentValues.SOURCE_SURFACE_MAIN,
            )
            .apply { this.clipData = clipData }
    }

    private fun writeOversizedHtml(file: File) {
        val header = "<!doctype html><html><body><p>$PREFIX_TARGET</p><!--".toByteArray()
        val footer = "--></body></html>".toByteArray()
        val omitted = "<p>omitted content</p>".toByteArray()
        val fillerBytes = HtmlPreviewerActivity.MAX_HTML_BYTES - header.size - footer.size
        check(fillerBytes > 0)
        val filler = ByteArray(8 * 1024) { 'x'.code.toByte() }

        file.outputStream().buffered().use { output ->
            output.write(header)
            var remaining = fillerBytes
            while (remaining > 0) {
                val length = minOf(filler.size, remaining)
                output.write(filler, 0, length)
                remaining -= length
            }
            output.write(footer)
            output.write(omitted)
        }
        check(file.length() > HtmlPreviewerActivity.MAX_HTML_BYTES)
    }

    private fun waitUntil(
        instrumentation: android.app.Instrumentation,
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
        private const val FIND_TIMEOUT_SECONDS = 10L
        private const val OVERSIZED_FILE_NAME = "oversized.html"
        private const val POLL_INTERVAL_MILLIS = 50L
        private const val PREFIX_TARGET = "html-previewer-truncated-prefix-target"
        private const val UI_TIMEOUT_SECONDS = 45L
    }
}

@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.app.Instrumentation
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.SystemClock
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
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class HtmlPreviewerSourceModeInstrumentationTest {

    @Test
    fun sourceModeTogglesRefreshesAndSurvivesActivityRecreationWithoutNetworkAccess() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val sharedPreferences = context.getSharedPreferences(
            HtmlPreviewerPreferences.PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )
        val hadStoredNetworkPreference = sharedPreferences.contains(
            HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES,
        )
        val originalNetworkPreference = HtmlPreviewerPreferences(context).loadNetworkImages
        val documentFile = HtmlPreviewerTestContentProvider.fileFor(context, TEST_FILE_NAME)
        var activity: HtmlPreviewerActivity? = null
        var recreationMonitor: Instrumentation.ActivityMonitor? = null

        try {
            documentFile.writeText(SOURCE_DOCUMENT)
            assertTrue(
                sharedPreferences.edit()
                    .putBoolean(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES, false)
                    .commit(),
            )
            activity = instrumentation.startActivitySync(
                previewerIntent(documentFile)
                    .setClass(context, HtmlPreviewerActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            ) as HtmlPreviewerActivity
            var viewer = requireNotNull(activity)
            assertViewerLoaded(instrumentation, viewer, "sanitized page")
            instrumentation.runOnMainSync {
                assertEquals(
                    viewer.getString(R.string.text_view_source),
                    sourceToggleItem(viewer).title.toString(),
                )
            }

            assertTrue(
                sharedPreferences.edit()
                    .putBoolean(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES, true)
                    .commit(),
            )
            val appUid = context.applicationInfo.uid
            val receivedBytesBefore = TrafficStats.getUidRxBytes(appUid)
            val transmittedBytesBefore = TrafficStats.getUidTxBytes(appUid)
            assertTrue(receivedBytesBefore != TrafficStats.UNSUPPORTED.toLong())
            assertTrue(transmittedBytesBefore != TrafficStats.UNSUPPORTED.toLong())

            instrumentation.runOnMainSync {
                assertTrue(viewer.onOptionsItemSelected(sourceToggleItem(viewer)))
            }
            assertViewerLoaded(instrumentation, viewer, "source view")
            assertSourceModeState(instrumentation, viewer)
            assertEquals(1, findCount(instrumentation, viewer, SOURCE_ONLY_MARKER))
            SystemClock.sleep(NETWORK_SETTLE_MILLIS)
            assertEquals(
                "Source view received external traffic",
                receivedBytesBefore,
                TrafficStats.getUidRxBytes(appUid),
            )
            assertEquals(
                "Source view transmitted external traffic",
                transmittedBytesBefore,
                TrafficStats.getUidTxBytes(appUid),
            )

            instrumentation.runOnMainSync {
                assertTrue(
                    viewer.onOptionsItemSelected(
                        viewer.findViewById<MaterialToolbar>(R.id.toolbar)
                            .menu
                            .findItem(R.id.action_refresh),
                    ),
                )
            }
            assertViewerLoaded(instrumentation, viewer, "refreshed source view")
            assertSourceModeState(instrumentation, viewer)
            assertEquals(1, findCount(instrumentation, viewer, SOURCE_ONLY_MARKER))

            recreationMonitor = instrumentation.addMonitor(
                HtmlPreviewerActivity::class.java.name,
                null,
                false,
            )
            instrumentation.runOnMainSync { viewer.recreate() }
            viewer = instrumentation.waitForMonitorWithTimeout(
                recreationMonitor,
                TimeUnit.SECONDS.toMillis(UI_TIMEOUT_SECONDS),
            ) as? HtmlPreviewerActivity
                ?: throw AssertionError("Timed out waiting for the recreated source viewer")
            activity = viewer
            assertViewerLoaded(instrumentation, viewer, "recreated source view")
            assertSourceModeState(instrumentation, viewer)
            assertEquals(1, findCount(instrumentation, viewer, SOURCE_ONLY_MARKER))

            assertTrue(
                sharedPreferences.edit()
                    .putBoolean(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES, false)
                    .commit(),
            )
            instrumentation.runOnMainSync {
                assertTrue(viewer.onOptionsItemSelected(sourceToggleItem(viewer)))
            }
            assertViewerLoaded(instrumentation, viewer, "restored sanitized page")
            instrumentation.runOnMainSync {
                assertEquals(
                    viewer.getString(R.string.text_view_source),
                    sourceToggleItem(viewer).title.toString(),
                )
                assertTrue(viewer.findViewById<View>(R.id.blocked_resources_banner).isShown)
                assertEquals(
                    viewer.getString(R.string.text_blocked_resource_count, 1),
                    viewer.findViewById<TextView>(R.id.blocked_resources_banner).text.toString(),
                )
            }
            assertEquals(0, findCount(instrumentation, viewer, SOURCE_ONLY_MARKER))
            assertEquals(1, findCount(instrumentation, viewer, PREVIEWER_MARKER))
        } finally {
            activity?.let { viewer -> instrumentation.runOnMainSync { viewer.finish() } }
            recreationMonitor?.let(instrumentation::removeMonitor)
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
            documentFile.delete()
        }
    }

    private fun assertViewerLoaded(
        instrumentation: Instrumentation,
        viewer: HtmlPreviewerActivity,
        description: String,
    ) {
        assertTrue(
            "Timed out waiting for the $description",
            waitUntil(instrumentation, UI_TIMEOUT_SECONDS) {
                viewer.findViewById<WebView>(R.id.previewer_web_view).isShown &&
                    !viewer.findViewById<View>(R.id.loading_indicator).isShown
            },
        )
    }

    private fun assertSourceModeState(
        instrumentation: Instrumentation,
        viewer: HtmlPreviewerActivity,
    ) {
        instrumentation.runOnMainSync {
            assertEquals(
                viewer.getString(R.string.text_view_previewer),
                sourceToggleItem(viewer).title.toString(),
            )
            assertFalse(viewer.findViewById<View>(R.id.blocked_resources_banner).isShown)
            viewer.findViewById<WebView>(R.id.previewer_web_view).settings.apply {
                assertFalse(blockNetworkImage)
                assertTrue(blockNetworkLoads)
            }
        }
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
                setFindListener(null)
                clearMatches()
            }
        }
        instrumentation.waitForIdleSync()
        SystemClock.sleep(FIND_RESET_MILLIS)
        instrumentation.runOnMainSync {
            viewer.findViewById<WebView>(R.id.previewer_web_view).apply {
                setFindListener { _, numberOfMatches, isDoneCounting ->
                    if (isDoneCounting) {
                        matchCount.set(numberOfMatches)
                        result.countDown()
                    }
                }
                findAllAsync(query)
            }
        }
        assertTrue(
            "Timed out finding $query",
            result.await(FIND_TIMEOUT_SECONDS, TimeUnit.SECONDS),
        )
        val count = matchCount.get()
        instrumentation.runOnMainSync {
            viewer.findViewById<WebView>(R.id.previewer_web_view).apply {
                setFindListener(null)
                clearMatches()
            }
        }
        return count
    }

    private fun previewerIntent(documentFile: File): Intent {
        val documentUri = HtmlPreviewerTestContentProvider.documentUri(TEST_FILE_NAME)
        val parentUri = HtmlPreviewerTestContentProvider.parentUri()
        val clipData = ClipData(
            ClipDescription("Source-mode HTML Previewer", arrayOf("text/html")),
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
            .putExtra(ExplorerActionIntentExtras.DISPLAY_NAME, TEST_FILE_NAME)
            .putExtra(ExplorerActionIntentExtras.SIZE, documentFile.length())
            .putExtra(ExplorerActionIntentExtras.PARENT_URI, parentUri)
            .putExtra(
                ExplorerActionIntentExtras.SOURCE_SURFACE,
                ExplorerActionIntentValues.SOURCE_SURFACE_MAIN,
            )
            .apply { this.clipData = clipData }
    }

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
        private const val FIND_TIMEOUT_SECONDS = 10L
        private const val FIND_RESET_MILLIS = 100L
        private const val NETWORK_SETTLE_MILLIS = 1_000L
        private const val POLL_INTERVAL_MILLIS = 50L
        private const val PREVIEWER_MARKER = "html-previewer-rendered-marker"
        private const val SOURCE_ONLY_MARKER = "html-previewer-source-only-marker"
        private const val TEST_FILE_NAME = "source-mode.html"
        private const val UI_TIMEOUT_SECONDS = 30L

        private val SOURCE_DOCUMENT = """
            <!doctype html>
            <html><body>
            <p>$PREVIEWER_MARKER</p>
            <script>$SOURCE_ONLY_MARKER</script>
            <img src="https://example.com/source-mode.png" onerror="steal()">
            </body></html>
        """.trimIndent()
    }
}

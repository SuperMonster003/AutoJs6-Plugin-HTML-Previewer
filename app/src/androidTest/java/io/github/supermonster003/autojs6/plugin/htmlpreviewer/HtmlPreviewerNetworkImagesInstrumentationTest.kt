@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.SystemClock
import android.view.View
import android.webkit.WebView
import android.widget.TextView
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
import java.io.File
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@RunWith(AndroidJUnit4::class)
class HtmlPreviewerNetworkImagesInstrumentationTest {

    @Test
    fun disabledNetworkImagesShowTheBlockedResourceCount() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val sharedPreferences = context.getSharedPreferences(
            HtmlPreviewerPreferences.PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )
        val hadStoredPreference = sharedPreferences.contains(
            HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES,
        )
        val originalPreference = HtmlPreviewerPreferences(context).loadNetworkImages
        val documentFile = HtmlPreviewerTestContentProvider.fileFor(context, TEST_FILE_NAME)
        val appUid = context.applicationInfo.uid
        val receivedBytesBefore = TrafficStats.getUidRxBytes(appUid)
        val transmittedBytesBefore = TrafficStats.getUidTxBytes(appUid)
        assertTrue(receivedBytesBefore != TrafficStats.UNSUPPORTED.toLong())
        assertTrue(transmittedBytesBefore != TrafficStats.UNSUPPORTED.toLong())
        var activity: HtmlPreviewerActivity? = null

        try {
            documentFile.writeText(
                """
                    <!doctype html><html><body>
                    <h1>Network image policy</h1>
                    <img src="https://example.com/one.png">
                    <img src="https://images.example.org/two.webp">
                    </body></html>
                """.trimIndent(),
            )
            assertTrue(
                sharedPreferences.edit()
                    .putBoolean(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES, false)
                    .commit(),
            )
            val viewer = instrumentation.startActivitySync(
                previewerIntent(documentFile)
                    .setClass(context, HtmlPreviewerActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            ) as HtmlPreviewerActivity
            activity = viewer

            assertTrue(
                "Timed out waiting for the network-blocked previewer",
                waitUntil(instrumentation, UI_TIMEOUT_SECONDS) {
                    viewer.findViewById<View>(R.id.blocked_resources_banner).isShown &&
                        viewer.findViewById<WebView>(R.id.previewer_web_view).isShown &&
                        !viewer.findViewById<View>(R.id.loading_indicator).isShown
                },
            )
            instrumentation.runOnMainSync {
                assertEquals(
                    viewer.getString(R.string.text_blocked_resource_count, 2),
                    viewer.findViewById<TextView>(R.id.blocked_resources_banner).text.toString(),
                )
                viewer.findViewById<WebView>(R.id.previewer_web_view).settings.apply {
                    assertFalse(blockNetworkImage)
                    assertTrue(blockNetworkLoads)
                }
            }
            SystemClock.sleep(NETWORK_SETTLE_MILLIS)
            assertEquals(
                "Network-disabled previewer received external traffic",
                receivedBytesBefore,
                TrafficStats.getUidRxBytes(appUid),
            )
            assertEquals(
                "Network-disabled previewer transmitted external traffic",
                transmittedBytesBefore,
                TrafficStats.getUidTxBytes(appUid),
            )
        } finally {
            activity?.let { viewer -> instrumentation.runOnMainSync { viewer.finish() } }
            restorePreference(
                sharedPreferences = sharedPreferences,
                hadStoredPreference = hadStoredPreference,
                originalPreference = originalPreference,
            )
            documentFile.delete()
        }
    }

    private fun previewerIntent(documentFile: File): Intent {
        val documentUri = HtmlPreviewerTestContentProvider.documentUri(TEST_FILE_NAME)
        val parentUri = HtmlPreviewerTestContentProvider.parentUri()
        val clipData = ClipData(
            ClipDescription("Network images HTML Previewer", arrayOf("text/html")),
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

    private fun restorePreference(
        sharedPreferences: android.content.SharedPreferences,
        hadStoredPreference: Boolean,
        originalPreference: Boolean,
    ) {
        sharedPreferences.edit().apply {
            if (hadStoredPreference) {
                putBoolean(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES, originalPreference)
            } else {
                remove(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES)
            }
        }.commit()
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
        private const val POLL_INTERVAL_MILLIS = 50L
        private const val NETWORK_SETTLE_MILLIS = 1_000L
        private const val TEST_FILE_NAME = "network-images.html"
        private const val UI_TIMEOUT_SECONDS = 30L
    }
}

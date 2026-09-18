@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ClipData
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.inspector.WindowInspector
import android.webkit.WebView
import android.widget.Button
import android.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.test.platform.app.InstrumentationRegistry
import com.google.android.material.switchmaterial.SwitchMaterial
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class PreviewerUiInstrumentationTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test
    fun systemBarsMatchVisibleBackgroundWhileScriptIsStillLoading() {
        assumeTrue(Build.VERSION.SDK_INT >= 29)
        val scriptRequested = CountDownLatch(1)
        val releaseScript = CountDownLatch(1)
        val request = request(HtmlPreviewerPlugin.PRIMARY_ACTION_ID)
        HtmlPreviewerTestContentProvider.fileFor(context, "appearance.html").writeText(
            """
            <html><head><style>body { margin: 0; min-height: 100vh; background: #123456; }</style></head>
            <body><script>
            var script = document.createElement('script');
            script.src = 'slow-chrome.js';
            document.head.appendChild(script);
            </script></body></html>
            """.trimIndent(),
        )
        HtmlPreviewerTestContentProvider.fileFor(context, "slow-chrome.js")
            .writeText("document.title = 'Script finished';")
        HtmlPreviewerTestContentProvider.beforeOpenFile = { name ->
            if (name == "slow-chrome.js") {
                scriptRequested.countDown()
                check(releaseScript.await(30, TimeUnit.SECONDS)) { "Script was never released" }
            }
        }
        val activity = instrumentation.startActivitySync(request) as HtmlPreviewerActivity
        try {
            await { activity.findViewById<View>(R.id.loading_indicator).visibility != View.VISIBLE }
            main {
                val item = PopupMenu(activity, View(activity)).menu.add(0, R.id.action_html_previewer_settings, 0, "")
                activity.onOptionsItemSelected(item)
            }
            await { dialogButton() != null }
            main {
                val button = requireNotNull(dialogButton())
                button.rootView.findViewById<SwitchMaterial>(R.id.interactive_mode).isChecked = true
                button.performClick()
            }
            assertTrue("The delayed script must be requested", scriptRequested.await(10, TimeUnit.SECONDS))
            val location = IntArray(2)
            main {
                activity.findViewById<WebView>(R.id.previewer_web_view).getLocationOnScreen(location)
            }
            // Observe the actual displayed background independently of WebView load callbacks.
            val deadline = SystemClock.uptimeMillis() + 10000
            var painted = false
            while (!painted && SystemClock.uptimeMillis() < deadline) {
                val screenshot = requireNotNull(instrumentation.uiAutomation.takeScreenshot())
                try {
                    painted = screenshot.getPixel(location[0] + 4, location[1] + 40) == 0xFF123456.toInt()
                } finally {
                    screenshot.recycle()
                }
                if (!painted) SystemClock.sleep(50)
            }
            assertTrue("The HTML background must be visible before releasing the script", painted)
            await("System bars must match the visible HTML before the script finishes") {
                (activity.findViewById<View>(R.id.toolbar).background as? android.graphics.drawable.ColorDrawable)?.color == 0xFF123456.toInt()
            }
            main {
                assertEquals("Page completion must still be pending", View.VISIBLE, activity.findViewById<View>(R.id.loading_indicator).visibility)
                val contentRoot = activity.findViewById<android.view.ViewGroup>(android.R.id.content).getChildAt(0)
                assertEquals(0xFF123456.toInt(), (contentRoot.background as android.graphics.drawable.ColorDrawable).color)
                if (Build.VERSION.SDK_INT < 35) {
                    assertEquals(0xFF123456.toInt(), activity.window.statusBarColor)
                    assertEquals(0xFF123456.toInt(), activity.window.navigationBarColor)
                }
                assertFalse(androidx.core.view.WindowInsetsControllerCompat(activity.window, activity.window.decorView).isAppearanceLightStatusBars)
            }
            releaseScript.countDown()
            await { activity.findViewById<View>(R.id.loading_indicator).visibility != View.VISIBLE }
        } finally {
            releaseScript.countDown()
            HtmlPreviewerTestContentProvider.beforeOpenFile = null
            main { activity.finish() }
            instrumentation.waitForIdleSync()
        }
    }

    @Test
    fun primaryAndOverflowIntentsBothOpenAndSettingsCanBeCancelled() {
        assumeTrue(Build.VERSION.SDK_INT >= 29)
        for (actionId in listOf(HtmlPreviewerPlugin.PRIMARY_ACTION_ID, HtmlPreviewerPlugin.ID)) {
            val request = request(actionId)
            assertNotNull(HtmlPreviewerIntentPolicy.resolve(request))
            val activity = instrumentation.startActivitySync(request) as HtmlPreviewerActivity
            try {
                await { activity.findViewById<WebView>(R.id.previewer_web_view)?.url?.startsWith("https://") == true }
                await {
                    (activity.findViewById<View>(R.id.toolbar).background as? android.graphics.drawable.ColorDrawable)?.color == 0xFF123456.toInt()
                }
                main {
                    // Android 15 enforces transparent system bars; the colored content root
                    // is then the visible system-bar background.
                    val contentRoot = activity.findViewById<android.view.ViewGroup>(android.R.id.content).getChildAt(0)
                    assertEquals(0xFF123456.toInt(), (contentRoot.background as android.graphics.drawable.ColorDrawable).color)
                    if (Build.VERSION.SDK_INT < 35) assertEquals(0xFF123456.toInt(), activity.window.statusBarColor)
                    if (Build.VERSION.SDK_INT < 35) assertEquals(0xFF123456.toInt(), activity.window.navigationBarColor)
                    assertFalse(androidx.core.view.WindowInsetsControllerCompat(activity.window, activity.window.decorView).isAppearanceLightStatusBars)
                    val item = PopupMenu(activity, View(activity)).menu.add(0, R.id.action_html_previewer_settings, 0, "")
                    assertTrue(activity.onOptionsItemSelected(item))
                }
                await { dialogButton() != null }
                main {
                    val button = requireNotNull(dialogButton())
                    val dialog = button.rootView
                    val toggle = dialog.findViewById<SwitchMaterial>(R.id.start_in_fullscreen_mode)
                    assertNotNull(toggle)
                    assertFalse(toggle.showText)
                    assertTrue(toggle.isClickable)
                    assertNotNull(toggle.thumbDrawable)
                    assertNotNull(toggle.trackDrawable)
                    val before = toggle.isChecked
                    toggle.performClick()
                    assertEquals(!before, toggle.isChecked)
                    val interactive = dialog.findViewById<SwitchMaterial>(R.id.interactive_mode)
                    interactive.performClick()
                    assertEquals(interactive.isChecked, !HtmlPreviewerPreferences(activity).interactiveMode)
                    val network = dialog.findViewById<SwitchMaterial>(R.id.load_network_images)
                    assertEquals(activity.getString(if (interactive.isChecked) R.string.text_load_network_resources else R.string.text_load_network_images), network.text.toString())
                    assertEquals(button.context.getColor(R.color.dialog_foreground), button.currentTextColor)
                    assertEquals(activity.getString(R.string.dialog_button_confirm), button.text.toString())
                    dialog.findViewById<Button>(android.R.id.button2).performClick()
                }
                await { dialogButton() == null }
                assertFalse(activity.isFinishing)

            } finally {
                main { activity.finish() }
                instrumentation.waitForIdleSync()
            }
        }
    }

    @Test
    fun dialogControlsUseBlackOrWhiteInBothNightModes() {
        for (night in listOf(false, true)) {
            val activity = instrumentation.startActivitySync(
                Intent(context, PreviewerAppearanceTestActivity::class.java)
                    .putExtra("dark_mode", night).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            ) as PreviewerAppearanceTestActivity
            try {
                main {
                    assertEquals(night, activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
                    val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(activity)
                        .setSingleChoiceItems(arrayOf("One", "Two"), 0, null)
                        .setPositiveButton(R.string.dialog_button_confirm, null)
                        .setNegativeButton(R.string.dialog_button_cancel, null)
                        .show()
                    try {
                        val color = if (night) 0xFFFFFFFF.toInt() else 0xFF202124.toInt()
                        assertEquals(color, dialog.getButton(android.content.DialogInterface.BUTTON_POSITIVE).currentTextColor)
                        assertEquals(color, dialog.getButton(android.content.DialogInterface.BUTTON_NEGATIVE).currentTextColor)
                        val checked = android.util.TypedValue()
                        val unchecked = android.util.TypedValue()
                        dialog.context.theme.resolveAttribute(androidx.appcompat.R.attr.colorControlActivated, checked, true)
                        dialog.context.theme.resolveAttribute(androidx.appcompat.R.attr.colorControlNormal, unchecked, true)
                        assertEquals(color, checked.data)
                        assertEquals(checked.data, unchecked.data)
                    } finally {
                        dialog.dismiss()
                    }
                }
            } finally {
                main { activity.finish() }
            }
        }
    }

    @Test
    fun hostConfigurationOverridesLanguageAndNightModeWithoutMutatingBaseResources() {
        val original = Configuration(context.resources.configuration)
        val snapshot = PreviewerHostAppearance("fr", true)
        val wrapped = snapshot.wrap(context)
        assertEquals("fr", wrapped.resources.configuration.locales[0].language)
        assertEquals(Configuration.UI_MODE_NIGHT_YES, wrapped.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK)
        assertEquals(original, context.resources.configuration)
        assertNull(PreviewerHostAppearance.fromBundle(Bundle()))
        assertNull(PreviewerHostAppearance.fromBundle(Bundle().apply { putInt("protocolVersion", 99) }))
    }

    @Test
    fun installedHostAppearanceIsAppliedToTheViewer() {
        val host = PreviewerHostAppearance.read(context)
        assumeTrue("Requires an official AutoJs6 host with the settings provider", host != null)
        val activity = instrumentation.startActivitySync(request(HtmlPreviewerPlugin.PRIMARY_ACTION_ID)) as HtmlPreviewerActivity
        try {
            assertEquals(host!!.languageTag, activity.resources.configuration.locales[0].toLanguageTag())
            assertEquals(host.darkMode, activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES)
        } finally {
            main { activity.finish() }
        }
    }

    private fun request(actionId: String): Intent {
        HtmlPreviewerTestContentProvider.fileFor(context, "appearance.html").writeText("<html><body style='background:#123456;color:white'>Settings regression test</body></html>")
        val documentUri = HtmlPreviewerTestContentProvider.documentUri("appearance.html")
        val parentUri = HtmlPreviewerTestContentProvider.parentUri()
        return Intent(context, HtmlPreviewerActivity::class.java)
            .setAction(ExplorerActionPluginActions.EXECUTE)
            .setDataAndType(documentUri, "text/html")
            .putExtra(ExplorerActionIntentExtras.ACTION_ID, actionId)
            .putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, HtmlPreviewerPlugin.PROTOCOL_VERSION)
            .putExtra(ExplorerActionIntentExtras.HOST_PACKAGE_NAME, "org.autojs.autojs6")
            .putExtra(ExplorerActionIntentExtras.HOST_VERSION_CODE, 5279L)
            .putExtra(ExplorerActionIntentExtras.SOURCE_SURFACE, ExplorerActionIntentValues.SOURCE_SURFACE_MAIN)
            .putExtra(ExplorerActionIntentExtras.PARENT_URI, parentUri)
            .putExtra(ExplorerActionIntentExtras.DISPLAY_NAME, "appearance.html")
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            .apply {
                clipData = ClipData.newRawUri("Document", documentUri).apply { addItem(ClipData.Item(parentUri)) }
            }
    }

    private fun dialogButton(): Button? = WindowInspector.getGlobalWindowViews()
        .firstNotNullOfOrNull { it.findViewById<Button>(android.R.id.button1)?.takeIf(View::isShown) }

    private fun await(message: String = "Timed out waiting for the viewer or settings dialog", condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 10000
        while (SystemClock.uptimeMillis() < deadline) {
            var ready = false
            main { ready = condition() }
            if (ready) return
            SystemClock.sleep(50)
        }
        fail(message)
    }

    private fun main(action: () -> Unit) = instrumentation.runOnMainSync(action)
}

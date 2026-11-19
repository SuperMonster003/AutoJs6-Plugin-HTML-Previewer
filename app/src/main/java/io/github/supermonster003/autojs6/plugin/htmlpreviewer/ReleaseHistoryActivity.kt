package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.material.color.MaterialColors

/**
 * Renders the bundled changelog for the current locale (English when that one is missing) as one group per released
 * version, under the title "Release history" with the plugin name as the subtitle. Like the previewer it follows the
 * language and night mode of AutoJs6 ([PreviewerHostActivity]) and uses the previewer's own page colors.
 */
internal class ReleaseHistoryActivity : PreviewerHostActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val palette = themePalette(this)
        // The night mode comes from AutoJs6, not from the system, so the system bar icons follow the palette.
        val statusBar = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT) { palette.dark }
        val navigationBar = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Below API 26 the navigation bar cannot show dark icons, so it keeps a readable dark background there.
            SystemBarStyle.dark(LEGACY_NAVIGATION_BAR)
        } else {
            SystemBarStyle.auto(LIGHT_NAVIGATION_SCRIM, DARK_NAVIGATION_SCRIM) { palette.dark }
        }
        enableEdgeToEdge(statusBarStyle = statusBar, navigationBarStyle = navigationBar)
        val candidates = ReleaseHistoryAssetPolicy.candidatesFor(resources.configuration.locales[0])
        val entries = loadReleaseHistory(candidates) { path ->
            assets.open(path).use { stream -> stream.readBytes().toString(Charsets.UTF_8) }
        }
        setContent {
            PluginReleaseHistoryTheme(palette) {
                ReleaseHistoryScreen(entries = entries, onBack = ::finish)
            }
        }
    }

    private companion object {
        const val LEGACY_NAVIGATION_BAR = 0xFF202124.toInt()

        // The scrims androidx.activity uses for three-button navigation by default.
        val LIGHT_NAVIGATION_SCRIM = Color.argb(0xE6, 0xFF, 0xFF, 0xFF)
        val DARK_NAVIGATION_SCRIM = Color.argb(0x80, 0x1B, 0x1B, 0x1B)

        // Neutral roles the previewer has no color of its own for (plugin settings guidelines, section 6).
        const val MUTED_LIGHT = 0xFF5F6368.toInt()
        const val MUTED_DARK = 0xFFB9BAC0.toInt()
        const val DIVIDER_LIGHT = 0xFFE0E3E7.toInt()
        const val DIVIDER_DARK = 0xFF34363A.toInt()

        /**
         * The page and top bar use the previewer's window background and text color; the accent roles come from the
         * previewer theme color through the HCT tones of the plugin settings guidelines (40 / 90 light, 80 / 30 dark).
         */
        fun themePalette(context: Context): ReleaseHistoryPalette {
            val dark = (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) ==
                Configuration.UI_MODE_NIGHT_YES
            val roles = MaterialColors.getColorRoles(context.getColor(R.color.color_primary), !dark)
            val background = context.getColor(R.color.window_background)
            return ReleaseHistoryPalette(
                dark = dark,
                primary = roles.accent,
                primaryContainer = roles.accentContainer,
                background = background,
                surface = background,
                onSurface = context.getColor(R.color.dialog_foreground),
                onSurfaceVariant = if (dark) MUTED_DARK else MUTED_LIGHT,
                outlineVariant = if (dark) DIVIDER_DARK else DIVIDER_LIGHT,
            )
        }
    }
}

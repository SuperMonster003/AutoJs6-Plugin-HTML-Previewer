package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.Context
import androidx.core.content.edit

class HtmlPreviewerPreferences(context: Context) {

    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    var startInFullscreenMode: Boolean
        get() = preferences.getBoolean(KEY_START_IN_FULLSCREEN_MODE, false)
        set(value) = preferences.edit { putBoolean(KEY_START_IN_FULLSCREEN_MODE, value) }

    var textZoom: Int
        get() = preferences.getInt(KEY_TEXT_ZOOM, DEFAULT_TEXT_ZOOM)
            .takeIf(::isSupportedTextZoom)
            ?: DEFAULT_TEXT_ZOOM
        set(value) {
            require(isSupportedTextZoom(value)) { "Unsupported text zoom: $value" }
            preferences.edit { putInt(KEY_TEXT_ZOOM, value) }
        }

    var themeMode: HtmlPreviewerThemeMode
        get() = HtmlPreviewerThemeMode.fromPreferenceValue(
            preferences.getString(KEY_THEME_MODE, null),
        )
        set(value) = preferences.edit { putString(KEY_THEME_MODE, value.preferenceValue) }

    var interactiveMode: Boolean
        get() = preferences.getBoolean(KEY_INTERACTIVE_MODE, false)
        set(value) = preferences.edit { putBoolean(KEY_INTERACTIVE_MODE, value) }

    var loadNetworkImages: Boolean
        get() = preferences.getBoolean(KEY_LOAD_NETWORK_IMAGES, true)
        set(value) = preferences.edit { putBoolean(KEY_LOAD_NETWORK_IMAGES, value) }

    companion object {
        const val MIN_TEXT_ZOOM = 75
        const val MAX_TEXT_ZOOM = 200
        const val TEXT_ZOOM_STEP = 25
        const val DEFAULT_TEXT_ZOOM = 100

        internal const val PREFERENCES_NAME = "html_previewer"
        private const val KEY_START_IN_FULLSCREEN_MODE = "start_in_fullscreen_mode"
        internal const val KEY_TEXT_ZOOM = "text_zoom"
        internal const val KEY_THEME_MODE = "theme_mode"
        internal const val KEY_INTERACTIVE_MODE = "interactive_mode"
        internal const val KEY_LOAD_NETWORK_IMAGES = "load_network_images"

        fun isSupportedTextZoom(value: Int): Boolean =
            value in MIN_TEXT_ZOOM..MAX_TEXT_ZOOM &&
                (value - MIN_TEXT_ZOOM) % TEXT_ZOOM_STEP == 0
    }
}

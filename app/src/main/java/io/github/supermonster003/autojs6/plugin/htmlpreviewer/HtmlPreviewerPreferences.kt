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

    companion object {
        private const val PREFERENCES_NAME = "html_previewer"
        private const val KEY_START_IN_FULLSCREEN_MODE = "start_in_fullscreen_mode"
    }
}

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.app.Activity
import android.os.Bundle
import android.os.Message
import android.os.Messenger

class HtmlPreviewerPreferencesProbeActivity : Activity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val preferences = HtmlPreviewerPreferences(this)
        val result = Bundle().apply {
            putInt(RESULT_TEXT_ZOOM, preferences.textZoom)
            putString(RESULT_THEME_MODE, preferences.themeMode.preferenceValue)
            putBoolean(RESULT_LOAD_NETWORK_IMAGES, preferences.loadNetworkImages)
        }
        intent.getParcelableExtra<Messenger>(EXTRA_REPLY_MESSENGER)
            ?.send(
                Message.obtain(null, RESULT_PREFERENCES).apply {
                    data = result
                },
            )
        finish()
    }

    companion object {
        const val EXTRA_REPLY_MESSENGER = "reply_messenger"
        const val RESULT_PREFERENCES = 1
        const val RESULT_TEXT_ZOOM = "text_zoom"
        const val RESULT_THEME_MODE = "theme_mode"
        const val RESULT_LOAD_NETWORK_IMAGES = "load_network_images"
    }
}

@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.Messenger
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class HtmlPreviewerPreferencesInstrumentationTest {

    @Test
    fun networkImagesDefaultToEnabledWhenNoValueIsStored() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = context.getSharedPreferences(
            HtmlPreviewerPreferences.PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )
        val hadStoredPreference = sharedPreferences.contains(
            HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES,
        )
        val originalPreference = HtmlPreviewerPreferences(context).loadNetworkImages

        try {
            assertTrue(
                sharedPreferences.edit()
                    .remove(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES)
                    .commit(),
            )
            assertTrue(HtmlPreviewerPreferences(context).loadNetworkImages)
        } finally {
            sharedPreferences.edit().apply {
                if (hadStoredPreference) {
                    putBoolean(
                        HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES,
                        originalPreference,
                    )
                } else {
                    remove(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES)
                }
            }.commit()
        }
    }

    @Test
    fun textZoomThemeAndNetworkImagesPersistIntoAFreshProcess() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val preferences = HtmlPreviewerPreferences(context)
        val sharedPreferences = context.getSharedPreferences(
            HtmlPreviewerPreferences.PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )
        val originalTextZoom = preferences.textZoom
        val originalThemeMode = preferences.themeMode
        val originalLoadNetworkImages = preferences.loadNetworkImages

        try {
            assertTrue(
                sharedPreferences.edit()
                    .putInt(HtmlPreviewerPreferences.KEY_TEXT_ZOOM, 175)
                    .putString(
                        HtmlPreviewerPreferences.KEY_THEME_MODE,
                        HtmlPreviewerThemeMode.DARK.preferenceValue,
                    )
                    .putBoolean(HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES, false)
                    .commit(),
            )

            assertEquals(175, HtmlPreviewerPreferences(context).textZoom)
            assertEquals(HtmlPreviewerThemeMode.DARK, HtmlPreviewerPreferences(context).themeMode)
            assertEquals(false, HtmlPreviewerPreferences(context).loadNetworkImages)

            val result = AtomicReference<Bundle?>()
            val resultReceived = CountDownLatch(1)
            val replyMessenger = Messenger(
                object : Handler(Looper.getMainLooper()) {
                    override fun handleMessage(message: Message) {
                        if (message.what == HtmlPreviewerPreferencesProbeActivity.RESULT_PREFERENCES) {
                            result.set(message.data)
                        }
                        resultReceived.countDown()
                    }
                },
            )
            context.startActivity(
                Intent(context, HtmlPreviewerPreferencesProbeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(
                        HtmlPreviewerPreferencesProbeActivity.EXTRA_REPLY_MESSENGER,
                        replyMessenger,
                    ),
            )

            assertTrue(
                "Timed out waiting for the fresh-process preference probe",
                resultReceived.await(PROBE_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            assertEquals(
                175,
                result.get()?.getInt(HtmlPreviewerPreferencesProbeActivity.RESULT_TEXT_ZOOM),
            )
            assertEquals(
                HtmlPreviewerThemeMode.DARK.preferenceValue,
                result.get()?.getString(HtmlPreviewerPreferencesProbeActivity.RESULT_THEME_MODE),
            )
            assertEquals(
                false,
                result.get()?.getBoolean(
                    HtmlPreviewerPreferencesProbeActivity.RESULT_LOAD_NETWORK_IMAGES,
                    true,
                ),
            )
        } finally {
            sharedPreferences.edit()
                .putInt(HtmlPreviewerPreferences.KEY_TEXT_ZOOM, originalTextZoom)
                .putString(
                    HtmlPreviewerPreferences.KEY_THEME_MODE,
                    originalThemeMode.preferenceValue,
                )
                .putBoolean(
                    HtmlPreviewerPreferences.KEY_LOAD_NETWORK_IMAGES,
                    originalLoadNetworkImages,
                )
                .commit()
        }
    }

    companion object {
        private const val PROBE_TIMEOUT_SECONDS = 10L
    }
}

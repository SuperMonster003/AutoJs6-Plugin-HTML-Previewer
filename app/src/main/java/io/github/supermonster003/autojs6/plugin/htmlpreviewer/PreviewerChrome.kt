@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.forEach
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

internal class PreviewerChrome(
    private val activity: AppCompatActivity,
    private val root: View,
    private val appBar: AppBarLayout,
    private val toolbar: MaterialToolbar,
    private val webView: WebView,
) {
    private var generation = 0L
    private var foreground = 0xFFFFFFFF.toInt()
    var background: Int = activity.getColor(R.color.window_background)
        private set

    init {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        appBar.fitsSystemWindows = false
        appBar.elevation = 0f
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        applyColor(background)
        ViewCompat.requestApplyInsets(root)
    }

    fun beginPage(fallback: Int) {
        generation++
        webView.setBackgroundColor(fallback)
        applyColor(fallback)
    }

    fun applyColor(color: Int) {
        background = color
        foreground = PreviewerChromeColors.foreground(color)
        root.setBackgroundColor(color)
        appBar.setBackgroundColor(color)
        toolbar.setBackgroundColor(color)
        toolbar.setTitleTextColor(foreground)
        toolbar.setSubtitleTextColor(foreground)
        root.findViewById<ViewGroup>(R.id.find_bar)?.let { findBar ->
            findBar.setBackgroundColor(color)
            for (index in 0 until findBar.childCount) {
                when (val child = findBar.getChildAt(index)) {
                    is TextView -> {
                        child.setTextColor(foreground)
                        child.setHintTextColor(foreground)
                        if (child is EditText) child.backgroundTintList = ColorStateList.valueOf(foreground)
                    }
                    is ImageView -> child.imageTintList = ColorStateList.valueOf(foreground)
                }
            }
        }
        toolbar.popupTheme = if (foreground == 0xFF000000.toInt()) {
            R.style.AppTheme_PopupOverlay_Light
        } else {
            R.style.AppTheme_PopupOverlay_Dark
        }
        activity.window.statusBarColor = color
        activity.window.navigationBarColor = color
        if (Build.VERSION.SDK_INT >= 28) activity.window.navigationBarDividerColor = color
        if (Build.VERSION.SDK_INT >= 29) {
            activity.window.isStatusBarContrastEnforced = false
            activity.window.isNavigationBarContrastEnforced = false
        }
        WindowInsetsControllerCompat(activity.window, activity.window.decorView).apply {
            isAppearanceLightStatusBars = foreground == 0xFF000000.toInt()
            // Android 7 cannot draw dark navigation icons.
            isAppearanceLightNavigationBars = foreground == 0xFF000000.toInt()
        }
        if (Build.VERSION.SDK_INT < 26 && foreground == 0xFF000000.toInt()) {
            activity.window.navigationBarColor = 0xFF202124.toInt()
        }
        tintIcons()
    }

    fun tintIcons() {
        toolbar.navigationIcon?.mutate()?.let { DrawableCompat.setTint(it, foreground) }
        toolbar.overflowIcon?.mutate()?.let { DrawableCompat.setTint(it, foreground) }
        toolbar.menu.forEach { item ->
            item.icon?.mutate()?.let { DrawableCompat.setTint(it, foreground) }
        }
    }

    /** Sample a rendered frame at page visibility/completion; never follow animations or scrolling. */
    fun samplePage() {
        val expected = generation
        webView.postVisualStateCallback(expected, object : WebView.VisualStateCallback() {
            override fun onComplete(requestId: Long) {
                if (expected != generation || activity.isDestroyed || !webView.isShown) return
                // A WebView visual-state callback precedes the next window frame. PixelCopy
                // must wait for that frame to be committed, otherwise it can read the blank page.
                if (Build.VERSION.SDK_INT >= 29 && webView.isHardwareAccelerated) {
                    webView.viewTreeObserver.registerFrameCommitCallback {
                        webView.post { capture(expected) }
                    }
                    webView.invalidate()
                } else {
                    // Older releases do not expose frame-commit callbacks. Their compositor
                    // can still return the blank frame after a UI draw, so allow it to settle.
                    webView.postOnAnimation { webView.postDelayed({ capture(expected) }, 250) }
                }
            }
        })
    }

    private fun capture(expected: Long) {
        if (expected != generation || activity.isDestroyed || webView.width == 0 || webView.height == 0) return
        val bitmap = createBitmap(32, 32)
        fun finish(success: Boolean) {
            try {
                if (!success || expected != generation || activity.isDestroyed) return
                val colors = buildList {
                    for (position in 2..29 step 3) {
                        add(bitmap[position, 1])
                        add(bitmap[position, 30])
                        add(bitmap[1, position])
                        add(bitmap[30, position])
                    }
                }
                val sampled = PreviewerChromeColors.representativeColor(colors, background)
                applyColor(sampled)
            } finally {
                bitmap.recycle()
            }
        }
        if (Build.VERSION.SDK_INT >= 26) {
            val location = IntArray(2)
            webView.getLocationInWindow(location)
            val rect = Rect(location[0], location[1], location[0] + webView.width, location[1] + webView.height)
            try {
                PixelCopy.request(activity.window, rect, bitmap, { finish(it == PixelCopy.SUCCESS) }, Handler(Looper.getMainLooper()))
            } catch (_: IllegalArgumentException) {
                finish(false)
            }
        } else {
            val canvas = Canvas(bitmap)
            canvas.scale(32f / webView.width, 32f / webView.height)
            webView.draw(canvas)
            finish(true)
        }
    }

    fun destroy() {
        generation++
    }
}

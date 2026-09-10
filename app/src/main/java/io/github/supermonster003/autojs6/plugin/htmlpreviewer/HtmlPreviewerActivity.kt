package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.github.supermonster003.autojs6.plugin.htmlpreviewer.databinding.ActivityHtmlPreviewerBinding
import io.github.supermonster003.autojs6.plugin.htmlpreviewer.databinding.DialogHtmlPreviewerSettingsBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class HtmlPreviewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHtmlPreviewerBinding
    private lateinit var previewRequest: HtmlPreviewerRequest
    private lateinit var preferences: HtmlPreviewerPreferences
    private lateinit var webController: HtmlPreviewerWebController

    private val renderer = HtmlPreviewerRenderer()
    private var loadJob: Job? = null
    private var loadGeneration = 0
    private var fullscreenMode = false

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (fullscreenMode) {
                setFullscreenMode(false)
                return
            }
            isEnabled = false
            onBackPressedDispatcher.onBackPressed()
            isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        previewRequest = HtmlPreviewerIntentPolicy.resolve(intent) ?: run {
            Toast.makeText(this, R.string.text_cannot_read_file, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        preferences = HtmlPreviewerPreferences(this)
        fullscreenMode = savedInstanceState?.getBoolean(STATE_FULLSCREEN_MODE)
            ?: preferences.startInFullscreenMode

        binding = ActivityHtmlPreviewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.previewWebView.setBackgroundColor(getColor(R.color.window_background))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = previewRequest.displayName
        }

        setFullscreenMode(fullscreenMode, invalidateMenu = false)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        webController = HtmlPreviewerWebController(
            context = this,
            webView = binding.previewWebView,
            resourceRoot = previewRequest.parentUri,
            onExternalLink = ::openExternalLink,
            onPageFinished = {
                binding.loadingIndicator.isVisible = false
            },
        )
        loadPreview()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_html_previewer, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_fullscreen_mode)?.isChecked = fullscreenMode
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> true.also { finish() }
        R.id.action_refresh -> true.also { loadPreview() }
        R.id.action_fullscreen_mode -> true.also { setFullscreenMode(!fullscreenMode) }
        R.id.action_html_previewer_settings -> true.also { showSettingsDialog() }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_FULLSCREEN_MODE, fullscreenMode)
        super.onSaveInstanceState(outState)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && fullscreenMode && ::binding.isInitialized) {
            applyFullscreenMode()
        }
    }

    override fun onDestroy() {
        loadGeneration++
        loadJob?.cancel()
        if (::webController.isInitialized) {
            webController.destroy()
        }
        super.onDestroy()
    }

    private fun loadPreview() {
        val generation = ++loadGeneration
        loadJob?.cancel()
        binding.loadingIndicator.isVisible = true
        binding.errorText.isVisible = false

        loadJob = lifecycleScope.launch {
            try {
                val source = withContext(Dispatchers.IO) {
                    HtmlPreviewerTextReader(contentResolver).read(
                        previewRequest.documentUri,
                        MAX_HTML_BYTES,
                    )
                }
                val html = withContext(Dispatchers.Default) {
                    renderer.render(source)
                }
                if (generation != loadGeneration) return@launch
                binding.previewWebView.isVisible = true
                webController.show(html)
            } catch (_: CancellationException) {
                // A newer load request superseded this one.
            } catch (error: Throwable) {
                if (generation == loadGeneration) {
                    showLoadError(error)
                }
            }
        }
    }

    private fun showLoadError(error: Throwable) {
        binding.loadingIndicator.isVisible = false
        binding.previewWebView.isVisible = false
        binding.errorText.isVisible = true
        binding.errorText.text = when (error) {
            is HtmlPreviewerTooLargeException -> getString(
                R.string.error_html_file_too_large,
                Formatter.formatShortFileSize(this, error.limitBytes.toLong()),
            )
            else -> getString(R.string.text_cannot_read_file)
        }
    }

    private fun showSettingsDialog() {
        val settingsBinding = DialogHtmlPreviewerSettingsBinding.inflate(layoutInflater).apply {
            startInFullscreenMode.isChecked = preferences.startInFullscreenMode
        }
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.text_settings)
            .setView(settingsBinding.root)
            .setNegativeButton(R.string.dialog_button_cancel, null)
            .setPositiveButton(R.string.dialog_button_confirm) { _, _ ->
                preferences.startInFullscreenMode = settingsBinding.startInFullscreenMode.isChecked
            }
            .show()
    }

    private fun setFullscreenMode(enabled: Boolean, invalidateMenu: Boolean = true) {
        val changed = fullscreenMode != enabled
        fullscreenMode = enabled
        binding.appBar.isVisible = !enabled
        applyFullscreenMode()
        if (changed && invalidateMenu) {
            invalidateOptionsMenu()
        }
    }

    private fun applyFullscreenMode() {
        WindowInsetsControllerCompat(window, window.decorView).apply {
            if (fullscreenMode) {
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                hide(WindowInsetsCompat.Type.systemBars())
            } else {
                show(WindowInsetsCompat.Type.systemBars())
            }
        }
        ViewCompat.requestApplyInsets(binding.root)
    }

    private fun openExternalLink(uri: Uri) {
        if (uri.scheme?.lowercase(Locale.ROOT) !in setOf("http", "https")) return
        try {
            startActivity(Intent(Intent.ACTION_VIEW, uri).addCategory(Intent.CATEGORY_BROWSABLE))
        } catch (_: ActivityNotFoundException) {
            Toast.makeText(this, R.string.text_cannot_open_link, Toast.LENGTH_SHORT).show()
        } catch (_: SecurityException) {
            Toast.makeText(this, R.string.text_cannot_open_link, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        const val MAX_HTML_BYTES = 8 * 1024 * 1024

        private const val STATE_FULLSCREEN_MODE = "fullscreen_mode"
    }
}

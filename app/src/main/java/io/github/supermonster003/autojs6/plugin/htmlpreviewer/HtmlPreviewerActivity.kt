package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.format.Formatter
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
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
import kotlin.math.roundToInt

class HtmlPreviewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHtmlPreviewerBinding
    private lateinit var previewerRequest: HtmlPreviewerRequest
    private lateinit var preferences: HtmlPreviewerPreferences
    private lateinit var webController: HtmlPreviewerWebController
    private lateinit var pdfExporter: HtmlPreviewerPdfExporter

    private val renderer = HtmlPreviewerRenderer()
    private var loadJob: Job? = null
    private var pdfExportJob: Job? = null
    private var loadGeneration = 0
    private var fullscreenMode = false
    private var findMode = false
    private var findMatchCount = 0
    private var pendingFind: Runnable? = null
    private var truncatedPreviewerRequested = false
    private var viewMode = HtmlPreviewerViewMode.RENDERED
    private var documentReady = false
    private var pdfExportInProgress = false

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            if (findMode) {
                closeFindBar()
                return
            }
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

        previewerRequest = HtmlPreviewerIntentPolicy.resolve(intent) ?: run {
            Toast.makeText(this, R.string.text_cannot_read_file, Toast.LENGTH_LONG).show()
            finish()
            return
        }
        preferences = HtmlPreviewerPreferences(this)
        fullscreenMode = savedInstanceState?.getBoolean(STATE_FULLSCREEN_MODE)
            ?: preferences.startInFullscreenMode
        truncatedPreviewerRequested = savedInstanceState
            ?.getBoolean(STATE_TRUNCATED_PREVIEWER_REQUESTED)
            ?: false
        viewMode = HtmlPreviewerViewMode.fromSavedState(
            savedInstanceState?.getString(STATE_VIEW_MODE),
        )

        binding = ActivityHtmlPreviewerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.previewerTruncatedButton.apply {
            text = getString(R.string.text_previewer_first_limit, formattedHtmlLimit())
            setOnClickListener {
                truncatedPreviewerRequested = true
                loadPreviewer()
            }
        }
        binding.previewerWebView.setBackgroundColor(getColor(R.color.window_background))
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = previewerRequest.displayName
        }

        setFullscreenMode(fullscreenMode, invalidateMenu = false)
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        webController = HtmlPreviewerWebController(
            context = this,
            webView = binding.previewerWebView,
            resourceRoot = previewerRequest.parentUri,
            onExternalLink = ::openExternalLink,
            onPageFinished = {
                documentReady = true
                binding.loadingIndicator.isVisible = false
                invalidateOptionsMenu()
                findCurrentQueryNow()
            },
            onFindResult = ::showFindResult,
            onBlockedResourceCountChanged = ::showBlockedResourceCount,
        )
        webController.setTextZoom(preferences.textZoom)
        webController.setThemeMode(preferences.themeMode)
        pdfExporter = HtmlPreviewerPdfExporter(this, previewerRequest.parentUri)
        setupFindBar(savedInstanceState)
        loadPreviewer()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_html_previewer, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.action_fullscreen_mode)?.isChecked = fullscreenMode
        menu.findItem(R.id.action_find_in_page)?.isVisible = !findMode
        menu.findItem(R.id.action_export_pdf)?.isEnabled =
            documentReady && !pdfExportInProgress
        menu.findItem(R.id.action_toggle_source)?.setTitle(
            when (viewMode) {
                HtmlPreviewerViewMode.RENDERED -> R.string.text_view_source
                HtmlPreviewerViewMode.SOURCE -> R.string.text_view_previewer
            },
        )
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> true.also { finish() }
        R.id.action_find_in_page -> true.also { openFindBar() }
        R.id.action_toggle_source -> true.also { toggleViewMode() }
        R.id.action_export_pdf -> true.also { requestPdfExport() }
        R.id.action_refresh -> true.also { loadPreviewer() }
        R.id.action_fullscreen_mode -> true.also { setFullscreenMode(!fullscreenMode) }
        R.id.action_html_previewer_settings -> true.also { showSettingsDialog() }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(STATE_FULLSCREEN_MODE, fullscreenMode)
        outState.putBoolean(STATE_FIND_MODE, findMode)
        outState.putBoolean(STATE_TRUNCATED_PREVIEWER_REQUESTED, truncatedPreviewerRequested)
        outState.putString(STATE_VIEW_MODE, viewMode.name)
        if (findMode) {
            outState.putString(STATE_FIND_QUERY, binding.findQuery.text?.toString().orEmpty())
        }
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
        pdfExportJob?.cancel()
        pendingFind?.let(binding.findQuery::removeCallbacks)
        if (::webController.isInitialized) {
            webController.destroy()
        }
        if (::pdfExporter.isInitialized) {
            pdfExporter.destroy()
        }
        super.onDestroy()
    }

    private fun loadPreviewer() {
        documentReady = false
        invalidateOptionsMenu()
        val generation = ++loadGeneration
        val themeMode = resolvedThemeMode(preferences.themeMode)
        val requestedNetworkImages = preferences.loadNetworkImages
        val interactive = preferences.interactiveMode &&
            previewerRequest.format == HtmlPreviewerDocumentFormat.HTML
        val currentViewMode = viewMode
        val loadNetworkImages = currentViewMode == HtmlPreviewerViewMode.RENDERED &&
            requestedNetworkImages
        val allowTruncated = previewerRequest.format == HtmlPreviewerDocumentFormat.HTML &&
            truncatedPreviewerRequested
        loadJob?.cancel()
        pendingFind?.let(binding.findQuery::removeCallbacks)
        pendingFind = null
        if (::webController.isInitialized) {
            webController.prepareForReload()
            webController.clearFindMatches()
            webController.setThemeMode(themeMode)
            webController.setLoadNetworkImages(loadNetworkImages)
        }
        resetFindResult()
        binding.loadingIndicator.isVisible = true
        binding.errorPanel.isVisible = false
        binding.truncationBanner.isVisible = false
        binding.blockedResourcesBanner.isVisible = false

        loadJob = lifecycleScope.launch {
            try {
                val loadedDocument = withContext(Dispatchers.IO) {
                    HtmlPreviewerDocumentReader(contentResolver).read(
                        request = previewerRequest,
                        maxBytes = MAX_HTML_BYTES,
                        allowTruncatedHtml = allowTruncated,
                    )
                }
                val formattedLimit = formattedHtmlLimit()
                val truncationMarker = if (loadedDocument.isTruncated) {
                    getString(R.string.text_truncated_previewer_marker, formattedLimit)
                } else {
                    null
                }
                val renderResult = withContext(Dispatchers.Default) {
                    renderer.renderResult(
                        html = if (currentViewMode == HtmlPreviewerViewMode.SOURCE) {
                            loadedDocument.sourceText
                        } else {
                            loadedDocument.previewerText
                        },
                        themeMode = themeMode,
                        truncationNotice = truncationMarker,
                        loadNetworkImages = requestedNetworkImages,
                        viewMode = currentViewMode,
                        interactive = interactive && !loadedDocument.isTruncated,
                        embeddedResources = loadedDocument.embeddedResources,
                        allowSiblingResources = loadedDocument.allowSiblingResources,
                    )
                }
                if (generation != loadGeneration) return@launch
                truncatedPreviewerRequested = loadedDocument.isTruncated
                binding.truncationBanner.apply {
                    isVisible = loadedDocument.isTruncated
                    if (loadedDocument.isTruncated) {
                        text = getString(R.string.text_truncated_previewer_banner, formattedLimit)
                    }
                }
                binding.previewerWebView.isVisible = true
                webController.show(
                    html = renderResult.html,
                    interactive = renderResult.interactive,
                    loadNetworkImages = renderResult.allowsNetworkImages,
                    initiallyBlockedResourceCount = renderResult.blockedNetworkResourceCount,
                    embeddedResources = renderResult.embeddedResources,
                    allowSiblingResources = renderResult.allowSiblingResources,
                )
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
        documentReady = false
        invalidateOptionsMenu()
        binding.loadingIndicator.isVisible = false
        binding.previewerWebView.isVisible = false
        binding.truncationBanner.isVisible = false
        binding.blockedResourcesBanner.isVisible = false
        binding.errorPanel.isVisible = true
        binding.previewerTruncatedButton.isVisible =
            error is HtmlPreviewerTooLargeException &&
                previewerRequest.format == HtmlPreviewerDocumentFormat.HTML
        binding.errorText.text = when (error) {
            is HtmlPreviewerTooLargeException -> getString(
                if (previewerRequest.format == HtmlPreviewerDocumentFormat.MHTML) {
                    R.string.error_mhtml_file_too_large
                } else {
                    R.string.error_html_file_too_large
                },
                Formatter.formatShortFileSize(this, error.limitBytes.toLong()),
            )
            else -> getString(R.string.text_cannot_read_file)
        }
    }

    private fun toggleViewMode() {
        viewMode = viewMode.toggled()
        invalidateOptionsMenu()
        loadPreviewer()
    }

    private fun requestPdfExport() {
        if (!documentReady || pdfExportInProgress) return
        pdfExportInProgress = true
        invalidateOptionsMenu()
        Toast.makeText(this, R.string.text_preparing_pdf, Toast.LENGTH_SHORT).show()

        val loadNetworkImages = preferences.loadNetworkImages
        val allowTruncated = previewerRequest.format == HtmlPreviewerDocumentFormat.HTML &&
            truncatedPreviewerRequested
        pdfExportJob?.cancel()
        pdfExportJob = lifecycleScope.launch {
            try {
                val loadedDocument = withContext(Dispatchers.IO) {
                    HtmlPreviewerDocumentReader(contentResolver).read(
                        request = previewerRequest,
                        maxBytes = MAX_HTML_BYTES,
                        allowTruncatedHtml = allowTruncated,
                    )
                }
                val truncationNotice = if (loadedDocument.isTruncated) {
                    getString(R.string.text_truncated_previewer_marker, formattedHtmlLimit())
                } else {
                    null
                }
                val printableDocument = withContext(Dispatchers.Default) {
                    renderer.renderResult(
                        html = loadedDocument.previewerText,
                        truncationNotice = truncationNotice,
                        loadNetworkImages = loadNetworkImages,
                        viewMode = HtmlPreviewerViewMode.RENDERED,
                        renderTarget = HtmlPreviewerRenderTarget.PRINT,
                        embeddedResources = loadedDocument.embeddedResources,
                        allowSiblingResources = loadedDocument.allowSiblingResources,
                    )
                }
                val started = pdfExporter.export(
                    document = printableDocument,
                    sourceDisplayName = previewerRequest.displayName,
                    onFinished = ::finishPdfExport,
                    onError = ::failPdfExport,
                )
                if (!started && pdfExportInProgress) {
                    failPdfExport()
                }
            } catch (_: CancellationException) {
                // Activity destruction cancels PDF preparation.
            } catch (_: Throwable) {
                failPdfExport()
            }
        }
    }

    private fun finishPdfExport() {
        pdfExportJob = null
        pdfExportInProgress = false
        invalidateOptionsMenu()
    }

    private fun failPdfExport() {
        finishPdfExport()
        Toast.makeText(this, R.string.text_cannot_export_pdf, Toast.LENGTH_LONG).show()
    }

    private fun formattedHtmlLimit(): String =
        Formatter.formatShortFileSize(this, MAX_HTML_BYTES.toLong())

    private fun resolvedThemeMode(mode: HtmlPreviewerThemeMode): HtmlPreviewerThemeMode =
        if (mode != HtmlPreviewerThemeMode.FOLLOW_SYSTEM) mode else {
            val night = resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
                android.content.res.Configuration.UI_MODE_NIGHT_YES
            if (night) HtmlPreviewerThemeMode.DARK else HtmlPreviewerThemeMode.LIGHT
        }

    private fun showSettingsDialog() {
        val initialTextZoom = preferences.textZoom
        val initialThemeMode = preferences.themeMode
        val initialLoadNetworkImages = preferences.loadNetworkImages
        val initialInteractiveMode = preferences.interactiveMode
        val builder = MaterialAlertDialogBuilder(this)
        val settingsBinding = DialogHtmlPreviewerSettingsBinding.inflate(android.view.LayoutInflater.from(builder.context)).apply {
            startInFullscreenMode.isChecked = preferences.startInFullscreenMode
            loadNetworkImages.isChecked = initialLoadNetworkImages
            interactiveMode.isChecked = initialInteractiveMode
            interactiveMode.isEnabled = previewerRequest.format == HtmlPreviewerDocumentFormat.HTML
            fun updateNetworkLabel() {
                loadNetworkImages.setText(if (interactiveMode.isChecked) R.string.text_load_network_resources else R.string.text_load_network_images)
            }
            updateNetworkLabel()
            interactiveMode.setOnCheckedChangeListener { _, _ -> updateNetworkLabel() }
            textZoom.value = initialTextZoom.toFloat()
            textZoom.setLabelFormatter { value -> "${value.roundToInt()}%" }
            updateTextZoomLabel(this, initialTextZoom)
            textZoom.addOnChangeListener { _, value, fromUser ->
                if (fromUser) {
                    val percentage = value.roundToInt()
                    updateTextZoomLabel(this, percentage)
                    webController.setTextZoom(percentage)
                }
            }
            themeMode.check(
                when (initialThemeMode) {
                    HtmlPreviewerThemeMode.FOLLOW_SYSTEM -> R.id.theme_follow_system
                    HtmlPreviewerThemeMode.LIGHT -> R.id.theme_light
                    HtmlPreviewerThemeMode.DARK -> R.id.theme_dark
                },
            )
            themeMode.setOnCheckedChangeListener { _, _ ->
                webController.setThemeMode(selectedThemeMode(this))
            }
        }
        val dialog = builder
            .setTitle(R.string.text_settings)
            .setView(settingsBinding.root)
            .setNegativeButton(R.string.dialog_button_cancel) { _, _ ->
                webController.setTextZoom(initialTextZoom)
                webController.setThemeMode(initialThemeMode)
            }
            .setPositiveButton(R.string.dialog_button_confirm) { _, _ ->
                val selectedThemeMode = selectedThemeMode(settingsBinding)
                val themeChanged = preferences.themeMode != selectedThemeMode
                val loadNetworkImagesChanged =
                    preferences.loadNetworkImages != settingsBinding.loadNetworkImages.isChecked
                val interactiveChanged = preferences.interactiveMode != settingsBinding.interactiveMode.isChecked
                preferences.interactiveMode = settingsBinding.interactiveMode.isChecked
                preferences.startInFullscreenMode = settingsBinding.startInFullscreenMode.isChecked
                preferences.textZoom = settingsBinding.textZoom.value.roundToInt()
                preferences.themeMode = selectedThemeMode
                preferences.loadNetworkImages = settingsBinding.loadNetworkImages.isChecked
                webController.setThemeMode(selectedThemeMode)
                if (themeChanged || loadNetworkImagesChanged || interactiveChanged) {
                    loadPreviewer()
                }
            }
            .create()
        dialog.setOnCancelListener {
            webController.setTextZoom(initialTextZoom)
            webController.setThemeMode(initialThemeMode)
        }
        dialog.show()
    }

    private fun selectedThemeMode(
        settingsBinding: DialogHtmlPreviewerSettingsBinding,
    ): HtmlPreviewerThemeMode = when (settingsBinding.themeMode.checkedRadioButtonId) {
        R.id.theme_light -> HtmlPreviewerThemeMode.LIGHT
        R.id.theme_dark -> HtmlPreviewerThemeMode.DARK
        else -> HtmlPreviewerThemeMode.FOLLOW_SYSTEM
    }

    private fun updateTextZoomLabel(
        settingsBinding: DialogHtmlPreviewerSettingsBinding,
        percentage: Int,
    ) {
        settingsBinding.textZoomLabel.text = getString(
            R.string.text_text_size_percentage,
            percentage,
        )
    }

    private fun showBlockedResourceCount(count: Int) {
        binding.blockedResourcesBanner.apply {
            isVisible = count > 0
            if (count > 0) {
                text = getString(R.string.text_blocked_resource_count, count)
            }
        }
    }

    private fun setupFindBar(savedInstanceState: Bundle?) {
        binding.findQuery.doAfterTextChanged { text ->
            scheduleFind(text?.toString().orEmpty())
        }
        binding.findQuery.setOnEditorActionListener { _, actionId, event ->
            val isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH
            val isEnterKey = event?.keyCode == KeyEvent.KEYCODE_ENTER &&
                event.action == KeyEvent.ACTION_DOWN
            if (isSearchAction || isEnterKey) {
                if (findMatchCount > 0) {
                    webController.findNext(true)
                } else {
                    findCurrentQueryNow()
                }
                true
            } else {
                false
            }
        }
        binding.findPreviousMatch.setOnClickListener { webController.findNext(false) }
        binding.findNextMatch.setOnClickListener { webController.findNext(true) }
        binding.closeFind.setOnClickListener { closeFindBar() }

        resetFindResult()
        val restoredFindMode = savedInstanceState?.getBoolean(STATE_FIND_MODE) == true
        findMode = restoredFindMode
        binding.findBar.isVisible = restoredFindMode
        if (restoredFindMode) {
            binding.findQuery.setText(savedInstanceState.getString(STATE_FIND_QUERY).orEmpty())
            binding.findQuery.setSelection(binding.findQuery.text?.length ?: 0)
        }
    }

    private fun openFindBar() {
        if (fullscreenMode) {
            setFullscreenMode(false)
        }
        findMode = true
        binding.findBar.isVisible = true
        invalidateOptionsMenu()
        binding.findQuery.post {
            binding.findQuery.requestFocus()
            getSystemService(InputMethodManager::class.java)
                .showSoftInput(binding.findQuery, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun closeFindBar() {
        pendingFind?.let(binding.findQuery::removeCallbacks)
        pendingFind = null
        findMode = false
        binding.findQuery.text?.clear()
        binding.findBar.isVisible = false
        webController.clearFindMatches()
        resetFindResult()
        getSystemService(InputMethodManager::class.java)
            .hideSoftInputFromWindow(binding.findQuery.windowToken, 0)
        binding.previewerWebView.requestFocus()
        invalidateOptionsMenu()
    }

    private fun scheduleFind(query: String) {
        pendingFind?.let(binding.findQuery::removeCallbacks)
        pendingFind = null
        resetFindResult()
        if (query.isEmpty()) {
            webController.clearFindMatches()
            return
        }
        Runnable {
            pendingFind = null
            if (findMode && binding.findQuery.text?.toString() == query) {
                webController.findAll(query)
            }
        }.also { find ->
            pendingFind = find
            binding.findQuery.postDelayed(find, FIND_DEBOUNCE_MILLIS)
        }
    }

    private fun findCurrentQueryNow() {
        if (!findMode) return
        pendingFind?.let(binding.findQuery::removeCallbacks)
        pendingFind = null
        val query = binding.findQuery.text?.toString().orEmpty()
        resetFindResult()
        webController.findAll(query)
    }

    private fun showFindResult(result: HtmlPreviewerFindResult) {
        if (!findMode || !result.isDoneCounting || binding.findQuery.text.isNullOrEmpty()) return
        findMatchCount = result.numberOfMatches
        val activeMatch = if (result.numberOfMatches == 0) {
            0
        } else {
            result.activeMatchOrdinal.coerceIn(0, result.numberOfMatches - 1) + 1
        }
        updateFindResult(activeMatch, result.numberOfMatches)
    }

    private fun resetFindResult() {
        findMatchCount = 0
        updateFindResult(activeMatch = 0, numberOfMatches = 0)
    }

    private fun updateFindResult(activeMatch: Int, numberOfMatches: Int) {
        binding.findMatchCount.text = getString(
            R.string.text_find_match_count,
            activeMatch,
            numberOfMatches,
        )
        val hasMatches = numberOfMatches > 0
        binding.findPreviousMatch.isEnabled = hasMatches
        binding.findNextMatch.isEnabled = hasMatches
        binding.findPreviousMatch.alpha = if (hasMatches) 1F else DISABLED_CONTROL_ALPHA
        binding.findNextMatch.alpha = if (hasMatches) 1F else DISABLED_CONTROL_ALPHA
    }

    private fun setFullscreenMode(enabled: Boolean, invalidateMenu: Boolean = true) {
        if (enabled && findMode) {
            closeFindBar()
        }
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

        private const val DISABLED_CONTROL_ALPHA = 0.38F
        private const val FIND_DEBOUNCE_MILLIS = 150L
        private const val STATE_FIND_MODE = "find_mode"
        private const val STATE_FIND_QUERY = "find_query"
        private const val STATE_FULLSCREEN_MODE = "fullscreen_mode"
        private const val STATE_TRUNCATED_PREVIEWER_REQUESTED = "truncated_previewer_requested"
        private const val STATE_VIEW_MODE = "view_mode"
    }
}

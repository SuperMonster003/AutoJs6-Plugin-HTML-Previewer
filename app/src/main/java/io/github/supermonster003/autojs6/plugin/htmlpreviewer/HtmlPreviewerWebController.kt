@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.Context
import android.net.Uri
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import java.io.ByteArrayInputStream
import java.net.URI
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class HtmlPreviewerWebController(
    context: Context,
    private val webView: WebView,
    resourceRoot: Uri,
    private val onExternalLink: (Uri) -> Unit,
    private val onPageFinished: () -> Unit = {},
    private val onFindResult: (HtmlPreviewerFindResult) -> Unit = {},
    private val onBlockedResourceCountChanged: (Int) -> Unit = {},
    private val awaitVisualStateBeforePageFinished: Boolean = true,
) {

    private val documentPathHandler = HtmlPreviewerDocumentPathHandler(context.contentResolver, resourceRoot)
    private val blockedNetworkRequestUrls = ConcurrentHashMap.newKeySet<String>()
    private var visualStateRequestId = 0L
    @Volatile
    private var pageGeneration = 0L
    @Volatile
    private var loadNetworkImages = true
    @Volatile
    private var interactive = false
    @Volatile
    private var initiallyBlockedResourceCount = 0

    private val assetLoader = WebViewAssetLoader.Builder()
        .setDomain(HtmlPreviewerWebOrigin.DOMAIN)
        .addPathHandler(
            HtmlPreviewerWebOrigin.PREVIEWER_ASSET_PATH_PREFIX,
            HtmlPreviewerAssetPathHandler(context.assets),
        )
        .addPathHandler(
            HtmlPreviewerWebOrigin.DOCUMENT_PATH_PREFIX,
            documentPathHandler,
        )
        .build()

    init {
        configureSettings()
        configureClient()
        configureFindListener()
    }

    private fun configureSettings() {
        if (BuildConfig.DEBUG) WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.apply {
            javaScriptEnabled = false
            javaScriptCanOpenWindowsAutomatically = false
            domStorageEnabled = false
            databaseEnabled = false
            setGeolocationEnabled(false)

            allowFileAccess = false
            allowContentAccess = false
            allowFileAccessFromFileURLs = false
            allowUniversalAccessFromFileURLs = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            blockNetworkImage = false
            blockNetworkLoads = false

            setSupportMultipleWindows(false)
            setSupportZoom(true)
            builtInZoomControls = true
            displayZoomControls = false
            mediaPlaybackRequiresUserGesture = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            saveFormData = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                safeBrowsingEnabled = true
            }
        }
        CookieManager.getInstance().setAcceptCookie(false)
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false)
        webView.isVerticalScrollBarEnabled = true
        webView.isHorizontalScrollBarEnabled = false
    }

    private fun configureClient() {
        webView.webViewClient = object : WebViewClientCompat() {
            override fun shouldInterceptRequest(
                view: WebView,
                request: WebResourceRequest,
            ): WebResourceResponse? {
                val uri = request.url
                if (interactive && !request.isForMainFrame && uri.scheme == "blob") return null
                if (
                    HtmlPreviewerRequestPolicy.shouldLetWebViewLoadDataResource(
                        uri.toString(),
                        request.isForMainFrame,
                    )
                ) {
                    return null
                }
                if (uri.scheme.equals("https", ignoreCase = true) && HtmlPreviewerWebOrigin.isDomain(uri.host)) {
                    return assetLoader.shouldInterceptRequest(uri) ?: forbidden()
                }
                if (
                    HtmlPreviewerRequestPolicy.shouldLetWebViewLoadHttpsSubresource(
                        uri.toString(),
                        request.isForMainFrame,
                        loadNetworkImages,
                    )
                ) {
                    return null
                }
                if (
                    !request.isForMainFrame &&
                    HtmlPreviewerRequestPolicy.isOutboundNetworkResource(uri.toString())
                ) {
                    recordBlockedNetworkResource(uri.toString())
                }
                return forbidden()
            }

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest,
            ): Boolean {
                if (!request.isForMainFrame) return true
                val uri = request.url
                if (isLocalAnchor(uri)) return false
                if (HtmlPreviewerWebOrigin.isDomain(uri.host)) return true
                if (request.hasGesture() && uri.scheme?.lowercase(Locale.ROOT) in EXTERNAL_LINK_SCHEMES) {
                    onExternalLink(uri)
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (url != "about:blank") {
                    if (!awaitVisualStateBeforePageFinished) {
                        onPageFinished()
                        return
                    }
                    val requestId = ++visualStateRequestId
                    view.postVisualStateCallback(
                        requestId,
                        object : WebView.VisualStateCallback() {
                            override fun onComplete(completedRequestId: Long) {
                                if (
                                    completedRequestId == visualStateRequestId &&
                                    view.url != "about:blank"
                                ) {
                                    onPageFinished()
                                }
                            }
                        },
                    )
                }
            }
        }
    }

    private fun configureFindListener() {
        webView.setFindListener { activeMatchOrdinal, numberOfMatches, isDoneCounting ->
            onFindResult(
                HtmlPreviewerFindResult(
                    activeMatchOrdinal = activeMatchOrdinal,
                    numberOfMatches = numberOfMatches,
                    isDoneCounting = isDoneCounting,
                ),
            )
        }
    }

    fun show(
        html: String,
        loadNetworkImages: Boolean = true,
        initiallyBlockedResourceCount: Int = 0,
        embeddedResources: Map<String, HtmlPreviewerEmbeddedResource> = emptyMap(),
        allowSiblingResources: Boolean = true,
        interactive: Boolean = false,
    ) {
        require(initiallyBlockedResourceCount >= 0) {
            "Blocked resource count cannot be negative: $initiallyBlockedResourceCount"
        }
        webView.stopLoading()
        this.interactive = interactive
        webView.settings.javaScriptEnabled = interactive
        webView.settings.domStorageEnabled = interactive
        visualStateRequestId++
        pageGeneration++
        blockedNetworkRequestUrls.clear()
        this.initiallyBlockedResourceCount = initiallyBlockedResourceCount
        setLoadNetworkImages(loadNetworkImages)
        documentPathHandler.updateDocument(
            html = html,
            loadNetworkImages = loadNetworkImages,
            embeddedResources = embeddedResources,
            allowSiblingResources = allowSiblingResources,
            interactive = interactive,
        )
        onBlockedResourceCountChanged(initiallyBlockedResourceCount)
        webView.loadUrl(HtmlPreviewerWebOrigin.DOCUMENT_URL)
    }

    fun prepareForReload() {
        webView.stopLoading()
        webView.settings.javaScriptEnabled = false
        webView.settings.domStorageEnabled = false
        webView.loadUrl("about:blank")
    }

    fun findAll(query: String) {
        if (query.isEmpty()) {
            clearFindMatches()
            return
        }
        webView.findAllAsync(query)
    }

    fun findNext(forward: Boolean) {
        webView.findNext(forward)
    }

    fun setTextZoom(percentage: Int) {
        require(HtmlPreviewerPreferences.isSupportedTextZoom(percentage)) {
            "Unsupported text zoom: $percentage"
        }
        webView.settings.textZoom = percentage
    }

    fun setThemeMode(themeMode: HtmlPreviewerThemeMode) {
        val backgroundColor = when (themeMode) {
            HtmlPreviewerThemeMode.FOLLOW_SYSTEM -> R.color.window_background
            HtmlPreviewerThemeMode.LIGHT -> R.color.previewer_background_light
            HtmlPreviewerThemeMode.DARK -> R.color.previewer_background_dark
        }
        webView.setBackgroundColor(ContextCompat.getColor(webView.context, backgroundColor))
    }

    fun setLoadNetworkImages(enabled: Boolean) {
        loadNetworkImages = enabled
        // Preserve images served from the previewer's own virtual origin. CSP, sanitization and
        // request interception decide which image URLs are admissible; blockNetworkLoads remains
        // the final WebView-level guard against every outbound request when this is disabled.
        webView.settings.blockNetworkImage = false
        webView.settings.blockNetworkLoads = !enabled
        if (!enabled) {
            webView.stopLoading()
        }
    }

    fun clearFindMatches() {
        webView.clearMatches()
    }

    fun destroy() {
        webView.settings.javaScriptEnabled = false
        webView.settings.domStorageEnabled = false
        visualStateRequestId++
        pageGeneration++
        blockedNetworkRequestUrls.clear()
        documentPathHandler.clearDocument()
        webView.setFindListener(null)
        webView.stopLoading()
        webView.webViewClient = WebViewClient()
        webView.webChromeClient = null
        webView.loadUrl("about:blank")
        webView.clearHistory()
        webView.clearFormData()
        webView.removeAllViews()
        webView.destroy()
    }

    private fun recordBlockedNetworkResource(url: String) {
        val generation = pageGeneration
        if (!blockedNetworkRequestUrls.add(url)) return
        webView.post {
            if (generation == pageGeneration) {
                onBlockedResourceCountChanged(
                    initiallyBlockedResourceCount + blockedNetworkRequestUrls.size,
                )
            }
        }
    }

    private fun isLocalAnchor(uri: Uri): Boolean {
        if (
            !uri.scheme.equals("https", ignoreCase = true) ||
            !HtmlPreviewerWebOrigin.isDomain(uri.host) ||
            uri.fragment == null
        ) {
            return false
        }
        return uri.path == HtmlPreviewerWebOrigin.DOCUMENT_PATH_PREFIX ||
            uri.path == Uri.parse(HtmlPreviewerWebOrigin.DOCUMENT_URL).path
    }

    companion object {
        private val EXTERNAL_LINK_SCHEMES = setOf("http", "https")

        private val FORBIDDEN_HEADERS = mapOf(
            "Cache-Control" to "no-store, max-age=0",
            "X-Content-Type-Options" to "nosniff",
        )

        private fun forbidden() = WebResourceResponse(
            "text/plain",
            Charsets.UTF_8.name(),
            403,
            "Forbidden",
            FORBIDDEN_HEADERS,
            ByteArrayInputStream(ByteArray(0)),
        )
    }
}

data class HtmlPreviewerFindResult(
    val activeMatchOrdinal: Int,
    val numberOfMatches: Int,
    val isDoneCounting: Boolean,
)

internal object HtmlPreviewerRequestPolicy {

    fun shouldLetWebViewLoadDataResource(
        url: String,
        isForMainFrame: Boolean,
    ): Boolean =
        !isForMainFrame &&
            url.startsWith("data:", ignoreCase = true) &&
            HtmlPreviewerUrlPolicy.isSafeResource(url)

    fun shouldLetWebViewLoadHttpsSubresource(
        url: String,
        isForMainFrame: Boolean,
        loadNetworkImages: Boolean = true,
    ): Boolean {
        if (isForMainFrame || !loadNetworkImages) return false
        val host = HtmlPreviewerUrlPolicy.safeRemoteHttpsHost(url) ?: return false
        return !HtmlPreviewerWebOrigin.isDomain(host)
    }

    fun isOutboundNetworkResource(url: String): Boolean {
        val uri = runCatching { URI(url.trim()) }.getOrNull() ?: return false
        if (uri.scheme?.lowercase(Locale.ROOT) !in setOf("http", "https")) return false
        return uri.host != null && !HtmlPreviewerWebOrigin.isDomain(uri.host)
    }
}

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintJob
import android.print.PrintManager
import android.webkit.WebView

class HtmlPreviewerPdfExporter(
    private val activity: Activity,
    private val resourceRoot: Uri,
    private val printJobLauncher: HtmlPreviewerPrintJobLauncher =
        SystemHtmlPreviewerPrintJobLauncher(activity),
) {

    private var activeSession: ExportSession? = null

    val isActive: Boolean
        get() = activeSession != null

    fun export(
        document: HtmlPreviewerRenderResult,
        sourceDisplayName: String,
        onFinished: () -> Unit = {},
        onError: () -> Unit = {},
    ): Boolean {
        require(document.viewMode == HtmlPreviewerViewMode.RENDERED) {
            "PDF export requires a sanitized page document"
        }
        require(document.renderTarget == HtmlPreviewerRenderTarget.PRINT) {
            "PDF export requires a print-target document"
        }
        if (activeSession != null) return false

        val documentName = HtmlPreviewerPdfPolicy.documentName(sourceDisplayName)
        var unownedWebView: WebView? = null
        return runCatching {
            lateinit var session: ExportSession
            val webView = WebView(activity).also { unownedWebView = it }
            val controller = HtmlPreviewerWebController(
                context = activity,
                webView = webView,
                resourceRoot = resourceRoot,
                onExternalLink = {},
                onPageFinished = { launchPrintJob(session) },
                awaitVisualStateBeforePageFinished = false,
            )
            session = ExportSession(
                documentName = documentName,
                webView = webView,
                controller = controller,
                onFinished = onFinished,
                onError = onError,
            )
            activeSession = session
            unownedWebView = null
            controller.setTextZoom(HtmlPreviewerPreferences.DEFAULT_TEXT_ZOOM)
            controller.setThemeMode(HtmlPreviewerThemeMode.LIGHT)
            controller.show(
                html = document.html,
                loadNetworkImages = document.allowsNetworkImages,
                initiallyBlockedResourceCount = document.blockedNetworkResourceCount,
                embeddedResources = document.embeddedResources,
                allowSiblingResources = document.allowSiblingResources,
            )
            true
        }.getOrElse {
            activeSession?.let { session -> finishSession(session, notify = false) }
                ?: unownedWebView?.destroy()
            onError()
            false
        }
    }

    fun destroy() {
        activeSession?.let { session -> finishSession(session, notify = false) }
    }

    private fun launchPrintJob(session: ExportSession) {
        if (activeSession !== session || session.handedToPrintFramework) return

        val launched = runCatching {
            val delegate = session.webView.createPrintDocumentAdapter(session.documentName)
            val adapter = FinishingPrintDocumentAdapter(delegate) {
                finishSession(session, notify = true)
            }
            session.adapter = adapter
            session.handedToPrintFramework = true
            printJobLauncher.launch(session.documentName, adapter)
        }.getOrDefault(false)
        if (!launched) {
            finishSession(session, notify = false)
            session.onError()
        }
    }

    private fun finishSession(
        session: ExportSession,
        notify: Boolean,
    ) {
        if (session.finished) return
        session.finished = true
        if (activeSession === session) {
            activeSession = null
        }
        session.adapter?.let { adapter -> runCatching(adapter::finishDelegate) }
        runCatching(session.controller::destroy)
        if (notify) {
            session.onFinished()
        }
    }

    private class ExportSession(
        val documentName: String,
        val webView: WebView,
        val controller: HtmlPreviewerWebController,
        val onFinished: () -> Unit,
        val onError: () -> Unit,
        var adapter: FinishingPrintDocumentAdapter? = null,
        var handedToPrintFramework: Boolean = false,
        var finished: Boolean = false,
    )
}

fun interface HtmlPreviewerPrintJobLauncher {
    fun launch(
        jobName: String,
        adapter: PrintDocumentAdapter,
    ): Boolean
}

private class SystemHtmlPreviewerPrintJobLauncher(
    private val activity: Activity,
) : HtmlPreviewerPrintJobLauncher {

    override fun launch(
        jobName: String,
        adapter: PrintDocumentAdapter,
    ): Boolean {
        if (!activity.packageManager.hasSystemFeature(PackageManager.FEATURE_PRINTING)) {
            return false
        }
        val printManager = activity.getSystemService(PrintManager::class.java) ?: return false
        val printJob: PrintJob? = printManager.print(
            jobName,
            adapter,
            PrintAttributes.Builder().build(),
        )
        return printJob != null
    }
}

private class FinishingPrintDocumentAdapter(
    private val delegate: PrintDocumentAdapter,
    private val onFinished: () -> Unit,
) : PrintDocumentAdapter() {

    private var delegateFinished = false

    override fun onStart() {
        delegate.onStart()
    }

    override fun onLayout(
        oldAttributes: PrintAttributes,
        newAttributes: PrintAttributes,
        cancellationSignal: CancellationSignal,
        callback: LayoutResultCallback,
        extras: Bundle,
    ) {
        delegate.onLayout(
            oldAttributes,
            newAttributes,
            cancellationSignal,
            callback,
            extras,
        )
    }

    override fun onWrite(
        pages: Array<out PageRange>,
        destination: android.os.ParcelFileDescriptor,
        cancellationSignal: CancellationSignal,
        callback: WriteResultCallback,
    ) {
        delegate.onWrite(pages, destination, cancellationSignal, callback)
    }

    override fun onFinish() {
        runCatching(::finishDelegate)
        onFinished()
    }

    fun finishDelegate() {
        if (delegateFinished) return
        delegateFinished = true
        delegate.onFinish()
    }
}

internal object HtmlPreviewerPdfPolicy {

    private const val PDF_EXTENSION = ".pdf"
    private const val MAX_DOCUMENT_NAME_LENGTH = 255

    fun documentName(displayName: String): String {
        val safeLeaf = displayName
            .replace('\\', '/')
            .substringAfterLast('/')
            .filterNot { character ->
                character == '\u0000' || character.code < 0x20 || character.code == 0x7f
            }
            .trim()
        val baseName = safeLeaf
            .substringBeforeLast('.', missingDelimiterValue = safeLeaf)
            .trim()
            .ifEmpty { "document" }
            .take(MAX_DOCUMENT_NAME_LENGTH - PDF_EXTENSION.length)
        return "$baseName$PDF_EXTENSION"
    }
}

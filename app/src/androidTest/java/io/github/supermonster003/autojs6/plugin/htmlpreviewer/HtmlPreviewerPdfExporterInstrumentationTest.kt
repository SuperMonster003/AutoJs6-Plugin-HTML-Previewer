@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.HtmlPreviewerPrintCallbackFactory
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.graphics.pdf.PdfRenderer
import android.util.Base64
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.ByteArrayOutputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

@RunWith(AndroidJUnit4::class)
class HtmlPreviewerPdfExporterInstrumentationTest {

    @Test
    fun sanitizedPrintTargetProducesAReadableMultiPagePdf() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val activity = AtomicReference<HtmlPreviewerWebViewTestActivity?>()
        val launcher = CapturingPrintJobLauncher()
        val exporter = AtomicReference<HtmlPreviewerPdfExporter?>()
        val exportFinished = CountDownLatch(1)
        val exportErrors = AtomicInteger()
        val outputFile = qaPdfFile(context)
        val mhtmlDocument = HtmlPreviewerMhtmlParser.parse(printableMhtmlArchive(redPngBytes()))
        val printableDocument = HtmlPreviewerRenderer().renderResult(
            html = mhtmlDocument.previewerText,
            themeMode = HtmlPreviewerThemeMode.DARK,
            loadNetworkImages = false,
            viewMode = HtmlPreviewerViewMode.RENDERED,
            renderTarget = HtmlPreviewerRenderTarget.PRINT,
            embeddedResources = mhtmlDocument.embeddedResources,
            allowSiblingResources = false,
        )

        assertEquals(HtmlPreviewerRenderTarget.PRINT, printableDocument.renderTarget)
        assertFalse(printableDocument.html.contains(SOURCE_ONLY_MARKER))
        assertEquals(1, printableDocument.embeddedResources.size)
        assertFalse(printableDocument.allowSiblingResources)
        outputFile.parentFile?.let { directory ->
            assertTrue(directory.mkdirs() || directory.isDirectory)
        }
        if (outputFile.exists()) {
            assertTrue(outputFile.delete())
        }

        try {
            activity.set(
                instrumentation.startActivitySync(
                    Intent(context, HtmlPreviewerWebViewTestActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ) as HtmlPreviewerWebViewTestActivity,
            )
            instrumentation.runOnMainSync {
                val testActivity = requireNotNull(activity.get())
                exporter.set(
                    HtmlPreviewerPdfExporter(
                        activity = testActivity,
                        resourceRoot = Uri.parse("content://html.previewer.test/root/document"),
                        printJobLauncher = launcher,
                    ).also { pdfExporter ->
                        assertTrue(
                            pdfExporter.export(
                                document = printableDocument,
                                sourceDisplayName = "sanitized-report.html",
                                onFinished = exportFinished::countDown,
                                onError = exportErrors::incrementAndGet,
                            ),
                        )
                    },
                )
            }

            assertTrue(
                "Timed out waiting for the offscreen print document",
                launcher.ready.await(PDF_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            assertEquals("sanitized-report.pdf", launcher.jobName.get())
            assertTrue(requireNotNull(exporter.get()).isActive)

            val adapter = requireNotNull(launcher.adapter.get())
            val layoutInfo = layoutPrintDocument(instrumentation, adapter)
            assertNotNull(layoutInfo)
            writePrintDocument(instrumentation, adapter, outputFile)
            instrumentation.runOnMainSync { adapter.onFinish() }

            assertTrue(
                "Timed out waiting for PDF export cleanup",
                exportFinished.await(PDF_TIMEOUT_SECONDS, TimeUnit.SECONDS),
            )
            assertEquals(0, exportErrors.get())
            assertFalse(requireNotNull(exporter.get()).isActive)
            assertTrue(outputFile.length() > MINIMUM_PDF_BYTES)
            assertEquals("%PDF-", outputFile.inputStream().use { input ->
                val header = ByteArray(5)
                assertEquals(header.size, input.read(header))
                header.toString(Charsets.US_ASCII)
            })
            assertPdfPagesAreReadable(outputFile)
        } finally {
            instrumentation.runOnMainSync {
                exporter.getAndSet(null)?.destroy()
                activity.getAndSet(null)?.finish()
            }
        }
    }

    @Test
    fun rejectedPrintLaunchReleasesTheOffscreenWebView() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val activity = AtomicReference<HtmlPreviewerWebViewTestActivity?>()
        val exporter = AtomicReference<HtmlPreviewerPdfExporter?>()
        val launchAttempted = CountDownLatch(1)
        val exportError = CountDownLatch(1)

        try {
            activity.set(
                instrumentation.startActivitySync(
                    Intent(context, HtmlPreviewerWebViewTestActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ) as HtmlPreviewerWebViewTestActivity,
            )
            instrumentation.runOnMainSync {
                val testActivity = requireNotNull(activity.get())
                exporter.set(
                    HtmlPreviewerPdfExporter(
                        activity = testActivity,
                        resourceRoot = Uri.parse("content://html.previewer.test/root/document"),
                        printJobLauncher = HtmlPreviewerPrintJobLauncher { _, _ ->
                            launchAttempted.countDown()
                            false
                        },
                    ).also { pdfExporter ->
                        assertTrue(
                            pdfExporter.export(
                                document = HtmlPreviewerRenderer().renderResult(
                                    html = "<p>Rejected print launch</p>",
                                    renderTarget = HtmlPreviewerRenderTarget.PRINT,
                                ),
                                sourceDisplayName = "rejected.html",
                                onError = exportError::countDown,
                            ),
                        )
                    },
                )
            }

            assertTrue(launchAttempted.await(PDF_TIMEOUT_SECONDS, TimeUnit.SECONDS))
            assertTrue(exportError.await(PDF_TIMEOUT_SECONDS, TimeUnit.SECONDS))
            assertFalse(requireNotNull(exporter.get()).isActive)
        } finally {
            instrumentation.runOnMainSync {
                exporter.getAndSet(null)?.destroy()
                activity.getAndSet(null)?.finish()
            }
        }
    }

    @Test
    fun destroyingExporterBeforeFrameworkFinishKeepsCleanupIdempotent() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val activity = AtomicReference<HtmlPreviewerWebViewTestActivity?>()
        val launcher = CapturingPrintJobLauncher()
        val exporter = AtomicReference<HtmlPreviewerPdfExporter?>()
        val finishedCallbacks = AtomicInteger()
        val exportErrors = AtomicInteger()

        try {
            activity.set(
                instrumentation.startActivitySync(
                    Intent(context, HtmlPreviewerWebViewTestActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                ) as HtmlPreviewerWebViewTestActivity,
            )
            instrumentation.runOnMainSync {
                exporter.set(
                    HtmlPreviewerPdfExporter(
                        activity = requireNotNull(activity.get()),
                        resourceRoot = Uri.parse("content://html.previewer.test/root/document"),
                        printJobLauncher = launcher,
                    ).also { pdfExporter ->
                        assertTrue(
                            pdfExporter.export(
                                document = HtmlPreviewerRenderer().renderResult(
                                    html = "<p>Lifecycle cleanup</p>",
                                    renderTarget = HtmlPreviewerRenderTarget.PRINT,
                                ),
                                sourceDisplayName = "lifecycle.html",
                                onFinished = finishedCallbacks::incrementAndGet,
                                onError = exportErrors::incrementAndGet,
                            ),
                        )
                    },
                )
            }

            assertTrue(launcher.ready.await(PDF_TIMEOUT_SECONDS, TimeUnit.SECONDS))
            instrumentation.runOnMainSync {
                requireNotNull(exporter.get()).destroy()
                requireNotNull(launcher.adapter.get()).onFinish()
            }

            assertFalse(requireNotNull(exporter.get()).isActive)
            assertEquals(0, finishedCallbacks.get())
            assertEquals(0, exportErrors.get())
        } finally {
            instrumentation.runOnMainSync {
                exporter.getAndSet(null)?.destroy()
                activity.getAndSet(null)?.finish()
            }
        }
    }

    private fun layoutPrintDocument(
        instrumentation: android.app.Instrumentation,
        adapter: PrintDocumentAdapter,
    ): PrintDocumentInfo? {
        val result = CountDownLatch(1)
        val info = AtomicReference<PrintDocumentInfo?>()
        val error = AtomicReference<CharSequence?>()
        val attributes = printAttributes()
        instrumentation.runOnMainSync {
            adapter.onStart()
            adapter.onLayout(
                attributes,
                attributes,
                CancellationSignal(),
                HtmlPreviewerPrintCallbackFactory.layout(
                    object : HtmlPreviewerPrintCallbackFactory.LayoutListener {
                        override fun onFinished(documentInfo: PrintDocumentInfo) {
                        info.set(documentInfo)
                        result.countDown()
                        }

                        override fun onFailed(message: CharSequence?) {
                            error.set(message ?: "Print layout failed")
                            result.countDown()
                        }

                        override fun onCancelled() {
                            error.set("Print layout was cancelled")
                            result.countDown()
                        }
                    },
                ),
                Bundle().apply {
                    putBoolean(PrintDocumentAdapter.EXTRA_PRINT_PREVIEW, false)
                },
            )
        }
        assertTrue(
            "Timed out laying out PDF content",
            result.await(PDF_TIMEOUT_SECONDS, TimeUnit.SECONDS),
        )
        assertEquals(null, error.get())
        return info.get()
    }

    private fun writePrintDocument(
        instrumentation: android.app.Instrumentation,
        adapter: PrintDocumentAdapter,
        outputFile: File,
    ) {
        val result = CountDownLatch(1)
        val error = AtomicReference<CharSequence?>()
        val descriptor = ParcelFileDescriptor.open(
            outputFile,
            ParcelFileDescriptor.MODE_CREATE or
                ParcelFileDescriptor.MODE_TRUNCATE or
                ParcelFileDescriptor.MODE_READ_WRITE,
        )
        instrumentation.runOnMainSync {
            adapter.onWrite(
                arrayOf(PageRange.ALL_PAGES),
                descriptor,
                CancellationSignal(),
                HtmlPreviewerPrintCallbackFactory.write(
                    object : HtmlPreviewerPrintCallbackFactory.WriteListener {
                        override fun onFinished() {
                            result.countDown()
                        }

                        override fun onFailed(message: CharSequence?) {
                            error.set(message ?: "Print write failed")
                            result.countDown()
                        }

                        override fun onCancelled() {
                            error.set("Print write was cancelled")
                            result.countDown()
                        }
                    },
                ),
            )
        }
        assertTrue(
            "Timed out writing PDF content",
            result.await(PDF_TIMEOUT_SECONDS, TimeUnit.SECONDS),
        )
        runCatching(descriptor::close)
        assertEquals(null, error.get())
    }

    private fun assertPdfPagesAreReadable(file: File) {
        val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        PdfRenderer(descriptor).use { renderer ->
            assertTrue("Expected a multi-page PDF", renderer.pageCount >= 2)
            listOf(0, renderer.pageCount - 1).distinct().forEach { pageIndex ->
                renderer.openPage(pageIndex).use { page ->
                    val bitmap = Bitmap.createBitmap(
                        page.width,
                        page.height,
                        Bitmap.Config.ARGB_8888,
                    ).apply { eraseColor(Color.WHITE) }
                    try {
                        page.render(
                            bitmap,
                            null,
                            null,
                            PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY,
                        )
                        var nonWhiteSamples = 0
                        for (y in 0 until bitmap.height step PIXEL_SAMPLE_STEP) {
                            for (x in 0 until bitmap.width step PIXEL_SAMPLE_STEP) {
                                if (bitmap.getPixel(x, y) and 0x00ffffff != 0x00ffffff) {
                                    nonWhiteSamples++
                                }
                            }
                        }
                        assertTrue(
                            "PDF page ${pageIndex + 1} appears blank",
                            nonWhiteSamples >= MINIMUM_NON_WHITE_SAMPLES,
                        )
                    } finally {
                        bitmap.recycle()
                    }
                }
            }
        }
    }

    private fun printAttributes(): PrintAttributes = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
        .setResolution(PrintAttributes.Resolution("qa", "PDF QA", 300, 300))
        .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
        .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
        .build()

    private class CapturingPrintJobLauncher : HtmlPreviewerPrintJobLauncher {
        val ready = CountDownLatch(1)
        val jobName = AtomicReference<String?>()
        val adapter = AtomicReference<PrintDocumentAdapter?>()

        override fun launch(
            jobName: String,
            adapter: PrintDocumentAdapter,
        ): Boolean {
            this.jobName.set(jobName)
            this.adapter.set(adapter)
            ready.countDown()
            return true
        }
    }

    companion object {
        private const val END_MARKER = "html-previewer-pdf-end-marker"
        private const val MINIMUM_NON_WHITE_SAMPLES = 20
        private const val MINIMUM_PDF_BYTES = 2_048L
        private const val PDF_TIMEOUT_SECONDS = 30L
        private const val PIXEL_SAMPLE_STEP = 4
        private const val SAFE_MARKER = "html-previewer-pdf-safe-marker"
        private const val SOURCE_ONLY_MARKER = "html-previewer-pdf-script-only-marker"
        private const val MHTML_BOUNDARY = "html-previewer-print-mhtml-boundary"

        fun qaPdfFile(context: Context): File = File(
            requireNotNull(context.getExternalFilesDir(null)),
            "pdf-qa/sanitized-report.pdf",
        )

        private fun largePrintableDocument(): String = buildString {
            append("<!doctype html><html><head><style>")
            append("body{font-family:sans-serif;line-height:1.5}")
            append("section{margin:0 0 18px;padding:12px;border:1px solid #bbb;break-inside:avoid}")
            append("h1{color:#174a7e} code{font-family:monospace}")
            append("</style></head><body>")
            append("<h1>Sanitized HTML Previewer PDF</h1>")
            append("<p><strong>$SAFE_MARKER</strong></p>")
            append("<img src=\"cid:print-image\" width=\"64\" height=\"64\">")
            append("<script>$SOURCE_ONLY_MARKER</script>")
            repeat(90) { index ->
                append("<section><h2>Section ${index + 1}</h2>")
                append("<p>This is a deterministic multi-page print sample. ")
                append("It verifies margins, wrapping, pagination, and readable text.</p>")
                append("<code>&lt;article data-index=\"")
                append(index + 1)
                append("\"&gt;safe static content&lt;/article&gt;</code></section>")
            }
            append("<p><strong>$END_MARKER</strong></p>")
            append("</body></html>")
        }

        private fun redPngBytes(): ByteArray {
            val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888).apply {
                eraseColor(Color.RED)
            }
            return try {
                ByteArrayOutputStream().use { output ->
                    check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
                    output.toByteArray()
                }
            } finally {
                bitmap.recycle()
            }
        }

        private fun printableMhtmlArchive(image: ByteArray): ByteArray = """
            MIME-Version: 1.0
            Content-Type: multipart/related; boundary="$MHTML_BOUNDARY"; type="text/html"

            --$MHTML_BOUNDARY
            Content-Type: text/html; charset=utf-8
            Content-Location: https://archive.example/report/index.html

            ${largePrintableDocument()}
            --$MHTML_BOUNDARY
            Content-Type: image/png
            Content-Transfer-Encoding: base64
            Content-ID: <print-image>
            Content-Location: https://archive.example/report/print-image.png

            ${Base64.encodeToString(image, Base64.NO_WRAP)}
            --$MHTML_BOUNDARY--
        """.trimIndent().replace("\n", "\r\n").toByteArray(Charsets.ISO_8859_1)
    }
}

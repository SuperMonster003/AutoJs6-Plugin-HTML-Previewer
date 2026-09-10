package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ContentResolver
import java.io.IOException

internal class HtmlPreviewerDocumentReader(
    private val contentResolver: ContentResolver,
) {

    fun read(
        request: HtmlPreviewerRequest,
        maxBytes: Int,
        allowTruncatedHtml: Boolean = false,
    ): HtmlPreviewerLoadedDocument = when (request.format) {
        HtmlPreviewerDocumentFormat.HTML -> {
            val result = HtmlPreviewerTextReader(contentResolver).read(
                uri = request.documentUri,
                maxBytes = maxBytes,
                allowTruncated = allowTruncatedHtml,
            )
            HtmlPreviewerLoadedDocument(
                sourceText = result.text,
                previewerText = result.text,
                isTruncated = result.isTruncated,
            )
        }

        HtmlPreviewerDocumentFormat.MHTML -> {
            val archive = contentResolver.openInputStream(request.documentUri)?.use { input ->
                HtmlPreviewerTextCodec.readBounded(input, maxBytes)
            } ?: throw IOException("Cannot open the MHTML Previewer document")
            val result = HtmlPreviewerMhtmlParser.parse(
                bytes = archive,
                limits = HtmlPreviewerMhtmlParser.Limits(maxDecodedBytes = maxBytes),
            )
            HtmlPreviewerLoadedDocument(
                sourceText = result.sourceText,
                previewerText = result.previewerText,
                isTruncated = false,
                embeddedResources = result.embeddedResources,
                allowSiblingResources = false,
            )
        }
    }
}

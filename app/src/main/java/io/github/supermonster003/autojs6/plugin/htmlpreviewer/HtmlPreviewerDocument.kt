package io.github.supermonster003.autojs6.plugin.htmlpreviewer

/** A resource served only from the previewer's virtual HTTPS origin. */
data class HtmlPreviewerEmbeddedResource(
    val bytes: ByteArray,
    val mimeType: String,
    val charset: String? = null,
)

internal data class HtmlPreviewerLoadedDocument(
    val sourceText: String,
    val previewerText: String,
    val isTruncated: Boolean,
    val embeddedResources: Map<String, HtmlPreviewerEmbeddedResource> = emptyMap(),
    val allowSiblingResources: Boolean = true,
)

internal data class HtmlPreviewerMhtmlDocument(
    val sourceText: String,
    val previewerText: String,
    val embeddedResources: Map<String, HtmlPreviewerEmbeddedResource>,
)

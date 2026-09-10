package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlPreviewerIntentPolicyTest {

    @Test
    fun htmlCanBeResolvedFromExtensionOrMimeType() {
        assertTrue(HtmlPreviewerIntentPolicy.isSupportedHtml("application/octet-stream", "index.html"))
        assertTrue(HtmlPreviewerIntentPolicy.isSupportedHtml("text/html; charset=utf-8", "previewer"))
        assertTrue(HtmlPreviewerIntentPolicy.isSupportedHtml(null, "index.XHTML"))
        assertTrue(HtmlPreviewerIntentPolicy.isSupportedHtml("application/xhtml+xml", "previewer"))
        assertEquals(
            HtmlPreviewerDocumentFormat.HTML,
            HtmlPreviewerIntentPolicy.documentFormat("application/octet-stream", "index.html"),
        )
    }

    @Test
    fun mhtmlCanBeResolvedFromArchiveExtensionOrExplicitMimeType() {
        assertEquals(
            HtmlPreviewerDocumentFormat.MHTML,
            HtmlPreviewerIntentPolicy.documentFormat("application/octet-stream", "saved-page.MHT"),
        )
        assertEquals(
            HtmlPreviewerDocumentFormat.MHTML,
            HtmlPreviewerIntentPolicy.documentFormat("multipart/related; boundary=archive", "previewer"),
        )
        assertEquals(
            HtmlPreviewerDocumentFormat.MHTML,
            HtmlPreviewerIntentPolicy.documentFormat("application/x-mimearchive", "previewer"),
        )
    }

    @Test
    fun unsupportedTypesAreRejected() {
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("text/html", "document.pdf"))
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("application/pdf", "document.pdf"))
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("text/plain", "notes.txt"))
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("text/markdown", "index.html"))
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("message/rfc822", "email"))
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("multipart/related", "email.eml"))
    }

    @Test
    fun displayNameNeverRetainsAPathOrControlCharacters() {
        assertEquals(
            "index.html",
            HtmlPreviewerIntentPolicy.sanitizeDisplayName("C:\\private\\folder/index.html\u0000"),
        )
        assertNull(HtmlPreviewerIntentPolicy.sanitizeDisplayName("\u0000\n\t"))
    }
}

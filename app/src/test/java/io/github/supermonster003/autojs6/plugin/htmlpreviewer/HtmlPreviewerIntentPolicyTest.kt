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
        assertTrue(HtmlPreviewerIntentPolicy.isSupportedHtml("text/html; charset=utf-8", "preview"))
        assertTrue(HtmlPreviewerIntentPolicy.isSupportedHtml(null, "index.XHTML"))
        assertTrue(HtmlPreviewerIntentPolicy.isSupportedHtml("application/xhtml+xml", "preview"))
    }

    @Test
    fun unsupportedTypesAreRejected() {
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("text/html", "document.pdf"))
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("application/pdf", "document.pdf"))
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("text/plain", "notes.txt"))
        assertFalse(HtmlPreviewerIntentPolicy.isSupportedHtml("text/markdown", "index.html"))
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

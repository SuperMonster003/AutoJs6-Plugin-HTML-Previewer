package io.github.supermonster003.autojs6.plugin.htmlpreview

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlPreviewIntentPolicyTest {

    @Test
    fun htmlCanBeResolvedFromExtensionOrMimeType() {
        assertTrue(HtmlPreviewIntentPolicy.isSupportedHtml("application/octet-stream", "index.html"))
        assertTrue(HtmlPreviewIntentPolicy.isSupportedHtml("text/html; charset=utf-8", "preview"))
        assertTrue(HtmlPreviewIntentPolicy.isSupportedHtml(null, "index.XHTML"))
        assertTrue(HtmlPreviewIntentPolicy.isSupportedHtml("application/xhtml+xml", "preview"))
    }

    @Test
    fun unsupportedTypesAreRejected() {
        assertFalse(HtmlPreviewIntentPolicy.isSupportedHtml("text/html", "document.pdf"))
        assertFalse(HtmlPreviewIntentPolicy.isSupportedHtml("application/pdf", "document.pdf"))
        assertFalse(HtmlPreviewIntentPolicy.isSupportedHtml("text/plain", "notes.txt"))
        assertFalse(HtmlPreviewIntentPolicy.isSupportedHtml("text/markdown", "index.html"))
    }

    @Test
    fun displayNameNeverRetainsAPathOrControlCharacters() {
        assertEquals(
            "index.html",
            HtmlPreviewIntentPolicy.sanitizeDisplayName("C:\\private\\folder/index.html\u0000"),
        )
        assertNull(HtmlPreviewIntentPolicy.sanitizeDisplayName("\u0000\n\t"))
    }
}

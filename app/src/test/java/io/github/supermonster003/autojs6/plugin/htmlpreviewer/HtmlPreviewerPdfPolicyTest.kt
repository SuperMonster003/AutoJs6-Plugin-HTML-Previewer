package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlPreviewerPdfPolicyTest {

    @Test
    fun documentNameReplacesTheSourceExtensionAndPreservesUsefulDots() {
        assertEquals(
            "report.final.pdf",
            HtmlPreviewerPdfPolicy.documentName("report.final.HTML"),
        )
        assertEquals(
            "阅读报告.pdf",
            HtmlPreviewerPdfPolicy.documentName("阅读报告"),
        )
    }

    @Test
    fun documentNameUsesOnlyTheSafeLeafAndFallsBackForAnEmptyBase() {
        assertEquals(
            "index.pdf",
            HtmlPreviewerPdfPolicy.documentName("folder\\nested/index.xhtml"),
        )
        assertEquals("document.pdf", HtmlPreviewerPdfPolicy.documentName(".html"))
        assertEquals("document.pdf", HtmlPreviewerPdfPolicy.documentName("\u0000\n"))
    }

    @Test
    fun documentNameStaysWithinTheCommonFileNameLimit() {
        val name = HtmlPreviewerPdfPolicy.documentName("a".repeat(300) + ".html")

        assertTrue(name.length <= 255)
        assertTrue(name.endsWith(".pdf"))
    }
}

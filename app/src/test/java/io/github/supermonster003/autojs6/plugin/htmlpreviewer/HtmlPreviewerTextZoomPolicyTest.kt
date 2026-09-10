package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlPreviewerTextZoomPolicyTest {

    @Test
    fun onlyConfiguredTextZoomStepsAreSupported() {
        listOf(75, 100, 125, 150, 175, 200).forEach { percentage ->
            assertTrue(HtmlPreviewerPreferences.isSupportedTextZoom(percentage))
        }
        listOf(74, 76, 99, 201).forEach { percentage ->
            assertFalse(HtmlPreviewerPreferences.isSupportedTextZoom(percentage))
        }
    }
}

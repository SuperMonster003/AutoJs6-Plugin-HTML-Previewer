package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.max
import kotlin.math.min

class PreviewerChromeColorsTest {
    @Test
    fun blackOrWhiteIconsAlwaysMeetNormalTextContrast() {
        for (red in 0..255 step 17) for (green in 0..255 step 17) for (blue in 0..255 step 17) {
            val background = 0xFF000000.toInt() or (red shl 16) or (green shl 8) or blue
            val foreground = PreviewerChromeColors.foreground(background)
            val first = PreviewerChromeColors.luminance(background)
            val second = PreviewerChromeColors.luminance(foreground)
            assertTrue((max(first, second) + 0.05) / (min(first, second) + 0.05) >= 4.5)
        }
    }

    @Test
    fun samplingPreservesSolidColorsAndResistsForegroundOutliers() {
        val background = 0xFF0D1117.toInt()
        assertEquals(background, PreviewerChromeColors.representativeColor(List(20) { background }, -1))
        assertEquals(background, PreviewerChromeColors.representativeColor(List(20) { background } + listOf(-1, -1), -1))
        assertEquals(background, PreviewerChromeColors.representativeColor(listOf(0), background))
        assertEquals(-1, PreviewerChromeColors.foreground(background))
        assertEquals(0xFF000000.toInt(), PreviewerChromeColors.foreground(0xFFFAF8F2.toInt()))
    }
}

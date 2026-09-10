package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import kotlin.math.pow

/** Pure color calculations shared by the toolbar and both system bars. */
internal object PreviewerChromeColors {
    fun luminance(color: Int): Double {
        fun channel(shift: Int): Double {
            val value = ((color ushr shift) and 255) / 255.0
            return if (value <= 0.04045) value / 12.92 else ((value + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(16) + 0.7152 * channel(8) + 0.0722 * channel(0)
    }

    fun foreground(background: Int): Int {
        val luminance = luminance(background)
        val blackContrast = (luminance + 0.05) / 0.05
        val whiteContrast = 1.05 / (luminance + 0.05)
        return if (blackContrast >= whiteContrast) 0xFF000000.toInt() else 0xFFFFFFFF.toInt()
    }

    fun representativeColor(colors: List<Int>, fallback: Int): Int {
        val opaque = colors.filter { (it ushr 24) >= 250 }
        if (opaque.isEmpty()) return fallback
        fun median(shift: Int) = opaque.map { (it ushr shift) and 255 }.sorted()[opaque.size / 2]
        return 0xFF000000.toInt() or (median(16) shl 16) or (median(8) shl 8) or median(0)
    }
}

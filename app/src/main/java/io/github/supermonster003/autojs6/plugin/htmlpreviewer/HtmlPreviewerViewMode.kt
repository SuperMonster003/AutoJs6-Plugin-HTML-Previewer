package io.github.supermonster003.autojs6.plugin.htmlpreviewer

enum class HtmlPreviewerViewMode {
    RENDERED,
    SOURCE;

    fun toggled(): HtmlPreviewerViewMode = when (this) {
        RENDERED -> SOURCE
        SOURCE -> RENDERED
    }

    companion object {
        fun fromSavedState(value: String?): HtmlPreviewerViewMode =
            entries.firstOrNull { mode -> mode.name == value } ?: RENDERED
    }
}

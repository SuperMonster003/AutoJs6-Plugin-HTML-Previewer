package io.github.supermonster003.autojs6.plugin.htmlpreviewer

enum class HtmlPreviewerThemeMode(
    val preferenceValue: String,
    val colorScheme: String,
) {
    FOLLOW_SYSTEM(
        preferenceValue = "system",
        colorScheme = "light dark",
    ),
    LIGHT(
        preferenceValue = "light",
        colorScheme = "light",
    ),
    DARK(
        preferenceValue = "dark",
        colorScheme = "dark",
    ),
    ;

    companion object {
        fun fromPreferenceValue(value: String?): HtmlPreviewerThemeMode =
            entries.firstOrNull { it.preferenceValue == value } ?: FOLLOW_SYSTEM
    }
}

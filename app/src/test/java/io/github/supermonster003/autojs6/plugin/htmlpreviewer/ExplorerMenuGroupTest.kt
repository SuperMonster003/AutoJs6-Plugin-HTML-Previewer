package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExplorerMenuGroupTest {
    @Test
    fun catalogNamesTheFirstLevelMenuAfterThePlugin() {
        // Host contract (explorer-action-protocol-v27.md): catalog-level keys, read from catalogs of every version.
        assertEquals(
            listOf("menuGroupLabelResourceName" to "app_name", "menuGroupLabelFallback" to "HTML Previewer"),
            HtmlPreviewerPlugin.MENU_GROUP_LABEL,
        )
    }

    @Test
    fun menuNameIsTheUntranslatedDisplayNameOfThePluginCenter() {
        val res = File("src/main/res")
        assertTrue(
            File(res, "values/strings_donottranslate.xml").readText()
                .contains("<string name=\"app_name\" translatable=\"false\">HTML Previewer</string>"),
        )
        res.listFiles { file -> file.isDirectory && file.name.startsWith("values-") }!!.forEach { directory ->
            directory.listFiles { file -> file.extension == "xml" }!!.forEach { file ->
                assertFalse("${directory.name}/${file.name}", file.readText().contains("name=\"app_name\""))
            }
        }
    }
}

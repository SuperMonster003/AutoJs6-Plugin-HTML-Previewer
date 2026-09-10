package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.Context
import android.os.Build
import android.os.Bundle
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.common.api.PluginInfo
import org.autojs.plugin.explorer.api.ExplorerActionCapabilityKeys
import org.autojs.plugin.explorer.api.ExplorerActionCatalogKeys
import org.autojs.plugin.explorer.api.ExplorerActionPluginIds
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.autojs.plugin.explorer.api.ExplorerActionValues

internal object HtmlPreviewerPlugin {
    const val ID = "html-previewer"
    const val VARIANT = "default"
    const val REQUIRED_HOST_VERSION = 5268L
    const val LABEL_RESOURCE_NAME = "action_html_previewer"
    const val LABEL_FALLBACK = "HTML preview"
    const val ACTIVITY_CLASS_NAME =
        "io.github.supermonster003.autojs6.plugin.htmlpreviewer.HtmlPreviewerActivity"

    val MIME_TYPES = arrayOf("text/html", "application/xhtml+xml")
    val EXTENSIONS = arrayOf(
        "html",
        "htm",
        "shtm",
        "shtml",
        "xht",
        "xhtml",
    )
}

internal fun Context.htmlPreviewerPluginInfo(): PluginInfo {
    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    return PluginInfo().apply {
        name = getString(R.string.app_name)
        description = getString(R.string.plugin_description)
        instruction = resources.openRawResource(R.raw.plugin_instruction).use { input ->
            input.bufferedReader(Charsets.UTF_8).readText().trim()
        }
        author = getString(R.string.plugin_author)
        collaborators = null
        versionName = packageInfo.versionName.orEmpty()
        versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
        versionDate = getString(R.string.plugin_version_date)
        id = HtmlPreviewerPlugin.ID
        engine = ExplorerActionPluginIds.ENGINE
        variant = HtmlPreviewerPlugin.VARIANT
        supportedAbis = emptyArray()
        capabilities = Bundle().apply {
            putLong(PluginCapabilityKeys.REQUIRES_HOST_VERSION, HtmlPreviewerPlugin.REQUIRED_HOST_VERSION)
            putInt(ExplorerActionCapabilityKeys.PROTOCOL_VERSION, ExplorerActionProtocol.VERSION)
        }
    }
}

internal fun htmlPreviewerActionCatalog(): Bundle {
    val action = Bundle().apply {
        putString(ExplorerActionCatalogKeys.ID, HtmlPreviewerPlugin.ID)
        putString(ExplorerActionCatalogKeys.LABEL_RESOURCE_NAME, HtmlPreviewerPlugin.LABEL_RESOURCE_NAME)
        putString(ExplorerActionCatalogKeys.LABEL_FALLBACK, HtmlPreviewerPlugin.LABEL_FALLBACK)
        putString(ExplorerActionCatalogKeys.ACTIVITY_CLASS_NAME, HtmlPreviewerPlugin.ACTIVITY_CLASS_NAME)
        putInt(ExplorerActionCatalogKeys.PRIORITY, 100)
        putInt(ExplorerActionCatalogKeys.TARGET_KIND, ExplorerActionValues.TARGET_FILE)
        putInt(ExplorerActionCatalogKeys.ACCESS_MODE, ExplorerActionValues.ACCESS_READ_ONLY)
        putInt(ExplorerActionCatalogKeys.PLACEMENT, ExplorerActionValues.PLACEMENT_OVERFLOW)
        putStringArrayList(
            ExplorerActionCatalogKeys.MIME_TYPES,
            ArrayList(HtmlPreviewerPlugin.MIME_TYPES.asList()),
        )
        putStringArrayList(
            ExplorerActionCatalogKeys.EXTENSIONS,
            ArrayList(HtmlPreviewerPlugin.EXTENSIONS.asList()),
        )
    }
    return Bundle().apply {
        putInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION, ExplorerActionProtocol.VERSION)
        putParcelableArrayList(ExplorerActionCatalogKeys.ACTIONS, arrayListOf(action))
    }
}

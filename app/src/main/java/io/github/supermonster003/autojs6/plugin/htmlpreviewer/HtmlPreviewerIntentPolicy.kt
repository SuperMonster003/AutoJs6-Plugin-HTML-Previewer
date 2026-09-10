package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import java.util.Locale

internal data class HtmlPreviewerRequest(
    val documentUri: Uri,
    val parentUri: Uri,
    val displayName: String,
)

/** Validates the complete, URI-only explorer action contract before any content is opened. */
internal object HtmlPreviewerIntentPolicy {

    private const val MAX_DISPLAY_NAME_LENGTH = 255

    private val htmlExtensions = setOf("html", "htm", "shtm", "shtml", "xht", "xhtml")
    private val htmlMimeTypes = setOf("text/html", "application/xhtml+xml")
    private val conflictingMimeTypes = setOf("text/markdown", "text/x-markdown")

    fun resolve(intent: Intent): HtmlPreviewerRequest? {
        if (intent.action != ExplorerActionPluginActions.EXECUTE) return null
        if (intent.getStringExtra(ExplorerActionIntentExtras.ACTION_ID) !in
            setOf(HtmlPreviewerPlugin.ID, HtmlPreviewerPlugin.PRIMARY_ACTION_ID)) return null
        if (
            intent.getIntExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, Int.MIN_VALUE) !=
            HtmlPreviewerPlugin.PROTOCOL_VERSION
        ) {
            return null
        }
        val hostVersionCode = intent.getLongExtra(
            ExplorerActionIntentExtras.HOST_VERSION_CODE,
            Long.MIN_VALUE,
        )
        if (!HtmlPreviewerExplorerCompatibility.acceptsHostVersionCode(hostVersionCode)) return null
        if (
            intent.getStringExtra(ExplorerActionIntentExtras.SOURCE_SURFACE) !=
            ExplorerActionIntentValues.SOURCE_SURFACE_MAIN
        ) {
            return null
        }

        val requiredFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
        if (intent.flags and requiredFlags != requiredFlags) return null

        val documentUri = intent.data?.takeIf(::isPlainContentUri) ?: return null
        val parentUri = intent.parcelableUriExtra(ExplorerActionIntentExtras.PARENT_URI)
            ?.takeIf(::isPlainContentUri)
            ?: return null
        if (!HtmlPreviewerPathPolicy.isDescendant(parentUri, documentUri)) return null

        val clipData = intent.clipData ?: return null
        if (clipData.itemCount != HtmlPreviewerExplorerCompatibility.LEGACY_CLIP_ITEM_COUNT) return null
        if (clipData.getItemAt(ExplorerActionIntentValues.CLIP_ITEM_TARGET_INDEX).uri != documentUri) return null
        if (clipData.getItemAt(ExplorerActionIntentValues.CLIP_ITEM_PARENT_INDEX).uri != parentUri) return null

        val suppliedName = intent.getStringExtra(ExplorerActionIntentExtras.DISPLAY_NAME)
        val displayName = sanitizeDisplayName(suppliedName)
            ?: sanitizeDisplayName(documentUri.lastPathSegment)
            ?: return null
        if (!isSupportedHtml(intent.type, displayName)) return null

        val declaredSize = intent.getLongExtra(ExplorerActionIntentExtras.SIZE, -1L)
        if (declaredSize > HtmlPreviewerActivity.MAX_HTML_BYTES) return null

        return HtmlPreviewerRequest(documentUri, parentUri, displayName)
    }

    fun isSupportedHtml(mimeType: String?, displayName: String): Boolean {
        val extension = displayName.substringAfterLast('.', missingDelimiterValue = "")
            .lowercase(Locale.ROOT)
        val hasSupportedExtension = extension in htmlExtensions
        val hasUnsupportedExtension = extension.isNotEmpty() && !hasSupportedExtension
        val normalizedMimeType = mimeType
            ?.substringBefore(';')
            ?.trim()
            ?.lowercase(Locale.ROOT)
        val hasSupportedMimeType = normalizedMimeType in htmlMimeTypes

        if (hasUnsupportedExtension) return false
        if (normalizedMimeType in conflictingMimeTypes) return false
        return hasSupportedExtension || hasSupportedMimeType
    }

    fun sanitizeDisplayName(value: String?): String? {
        val leaf = value
            ?.replace('\\', '/')
            ?.substringAfterLast('/')
            ?.filterNot { it == '\u0000' || it.code < 0x20 || it.code == 0x7f }
            ?.trim()
            ?.take(MAX_DISPLAY_NAME_LENGTH)
            .orEmpty()
        return leaf.takeIf(String::isNotEmpty)
    }

    private fun isPlainContentUri(uri: Uri): Boolean =
        uri.scheme.equals(ContentResolver.SCHEME_CONTENT, ignoreCase = true) &&
            !uri.authority.isNullOrBlank() &&
            uri.query == null &&
            uri.fragment == null

    @Suppress("DEPRECATION")
    private fun Intent.parcelableUriExtra(name: String): Uri? = getParcelableExtra(name)
}

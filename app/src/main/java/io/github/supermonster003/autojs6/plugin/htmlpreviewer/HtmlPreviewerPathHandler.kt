package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ContentResolver
import android.content.res.AssetManager
import android.net.Uri
import android.webkit.WebResourceResponse
import androidx.webkit.WebViewAssetLoader
import java.io.ByteArrayInputStream
import java.io.IOException
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference

class HtmlPreviewerDocumentPathHandler(
    private val contentResolver: ContentResolver,
    rootUri: Uri,
) : WebViewAssetLoader.PathHandler {

    private val resourceRoot = HtmlPreviewerPathPolicy.normalizeRoot(rootUri)
        ?: throw IllegalArgumentException("HTML Previewer resource root must be a content URI without a query or fragment")
    private val document = AtomicReference<Document?>(null)

    fun updateDocument(
        html: String,
        loadNetworkImages: Boolean,
        embeddedResources: Map<String, HtmlPreviewerEmbeddedResource> = emptyMap(),
        allowSiblingResources: Boolean = true,
        interactive: Boolean = false,
    ) {
        val resourceSnapshot = embeddedResources.mapValues { (path, resource) ->
            require(HtmlPreviewerPathPolicy.isSafeRelativePath(path)) {
                "Unsafe embedded previewer resource path: $path"
            }
            require(HtmlPreviewerPathPolicy.mimeType(path) == resource.mimeType) {
                "Embedded previewer resource MIME type does not match its path: $path"
            }
            resource.copy(bytes = resource.bytes.copyOf())
        }
        document.set(
            Document(
                bytes = html.toByteArray(Charsets.UTF_8),
                contentSecurityPolicy = HtmlPreviewerSecurityPolicy.contentSecurityPolicy(loadNetworkImages, interactive),
                embeddedResources = resourceSnapshot,
                allowSiblingResources = allowSiblingResources,
                interactive = interactive,
            ),
        )
    }

    fun clearDocument() {
        document.set(null)
    }

    override fun handle(path: String): WebResourceResponse {
        val currentDocument = document.get() ?: return notFound()
        if (path == HtmlPreviewerWebOrigin.DOCUMENT_FILE_NAME) {
            return WebResourceResponse(
                "text/html",
                Charsets.UTF_8.name(),
                200,
                "OK",
                documentResponseHeaders(currentDocument.contentSecurityPolicy),
                ByteArrayInputStream(currentDocument.bytes),
            )
        }

        currentDocument.embeddedResources[path]?.let { resource ->
            return WebResourceResponse(
                resource.mimeType,
                resource.charset,
                200,
                "OK",
                RESPONSE_HEADERS,
                ByteArrayInputStream(resource.bytes),
            )
        }
        if (!currentDocument.allowSiblingResources) return notFound()

        val resource = HtmlPreviewerPathPolicy.resolve(resourceRoot, path, currentDocument.interactive) ?: return notFound()
        val input = try {
            contentResolver.openInputStream(resource.uri) ?: return notFound()
        } catch (_: IOException) {
            return notFound()
        } catch (_: SecurityException) {
            return notFound()
        } catch (_: RuntimeException) {
            return notFound()
        }
        return WebResourceResponse(
            resource.mimeType,
            null,
            200,
            "OK",
            RESPONSE_HEADERS,
            input,
        )
    }

    private data class Document(
        val bytes: ByteArray,
        val contentSecurityPolicy: String,
        val embeddedResources: Map<String, HtmlPreviewerEmbeddedResource>,
        val allowSiblingResources: Boolean,
        val interactive: Boolean,
    )
}

class HtmlPreviewerAssetPathHandler(
    private val assetManager: AssetManager,
) : WebViewAssetLoader.PathHandler {

    override fun handle(path: String): WebResourceResponse {
        val assetPath = path.takeIf(HtmlPreviewerAssetPolicy::isAllowed) ?: return notFound()
        val input = try {
            assetManager.open("$ASSET_ROOT/$assetPath", AssetManager.ACCESS_STREAMING)
        } catch (_: IOException) {
            return notFound()
        } catch (_: SecurityException) {
            return notFound()
        }
        return WebResourceResponse(
            "text/css",
            Charsets.UTF_8.name(),
            200,
            "OK",
            RESPONSE_HEADERS,
            input,
        )
    }

    companion object {
        private const val ASSET_ROOT = "html-previewer"
    }
}

internal object HtmlPreviewerAssetPolicy {
    private val allowedAssets = setOf("html-base.css")

    fun isAllowed(path: String): Boolean = path in allowedAssets
}

internal object HtmlPreviewerPathPolicy {

    data class Resource(
        val uri: Uri,
        val mimeType: String,
    )

    private const val MAX_PATH_LENGTH = 2048

    fun normalizeRoot(rootUri: Uri): Uri? {
        if (!rootUri.scheme.equals(ContentResolver.SCHEME_CONTENT, ignoreCase = true)) return null
        if (rootUri.authority.isNullOrBlank() || rootUri.query != null || rootUri.fragment != null) return null
        return rootUri.buildUpon().clearQuery().fragment(null).build()
    }

    fun resolve(rootUri: Uri, path: String, allowScripts: Boolean = false): Resource? {
        val root = normalizeRoot(rootUri) ?: return null
        if (!isSafeRelativePath(path)) return null
        val mimeType = mimeType(path) ?: if (allowScripts) scriptMimeType(path) else null
        if (mimeType == null) return null
        val builder = root.buildUpon()
        path.split('/').forEach(builder::appendPath)
        val uri = builder.build()
        if (!isDescendant(root, uri)) return null
        return Resource(uri, mimeType)
    }

    fun isDescendant(rootUri: Uri, candidateUri: Uri): Boolean {
        val root = normalizeRoot(rootUri) ?: return false
        if (!candidateUri.scheme.equals(root.scheme, ignoreCase = true)) return false
        if (!candidateUri.authority.equals(root.authority, ignoreCase = true)) return false
        if (candidateUri.query != null || candidateUri.fragment != null) return false
        val rootSegments = root.pathSegments
        val candidateSegments = candidateUri.pathSegments
        return candidateSegments.size > rootSegments.size &&
            candidateSegments.take(rootSegments.size) == rootSegments &&
            candidateSegments.drop(rootSegments.size).all(::isSafeUriSegment)
    }

    fun isSafeRelativePath(path: String): Boolean {
        if (path.isEmpty() || path.length > MAX_PATH_LENGTH || path.startsWith('/')) return false
        if (path.any { it == '\\' || it == '\u0000' || it == '%' || it == '?' || it == '#' || it == ':' }) {
            return false
        }
        if (path.any { it.code < 0x20 || it.code == 0x7f }) return false
        return path.split('/').none { it.isEmpty() || it == "." || it == ".." }
    }

    fun mimeType(path: String): String? = when (path.substringAfterLast('.').lowercase(Locale.ROOT)) {
        "css" -> "text/css"
        "png" -> "image/png"
        "jpg", "jpeg" -> "image/jpeg"
        "gif" -> "image/gif"
        "webp" -> "image/webp"
        "avif" -> "image/avif"
        "bmp" -> "image/bmp"
        "ico" -> "image/x-icon"
        "svg" -> "image/svg+xml"
        "heic" -> "image/heic"
        "heif" -> "image/heif"
        "woff" -> "font/woff"
        "woff2" -> "font/woff2"
        "ttf" -> "font/ttf"
        "otf" -> "font/otf"
        "mp4", "m4v" -> "video/mp4"
        "webm" -> "video/webm"
        "ogv" -> "video/ogg"
        "mp3" -> "audio/mpeg"
        "m4a" -> "audio/mp4"
        "ogg", "oga" -> "audio/ogg"
        "wav" -> "audio/wav"
        else -> null
    }

    private fun scriptMimeType(path: String): String? = when (path.substringAfterLast('.').lowercase(Locale.ROOT)) {
        "js", "mjs" -> "text/javascript"
        "json" -> "application/json"
        "wasm" -> "application/wasm"
        else -> null
    }

    private fun isSafeUriSegment(segment: String): Boolean =
        segment.isNotEmpty() &&
            segment != "." &&
            segment != ".." &&
            segment.none { it == '/' || it == '\\' || it == '\u0000' || it.code < 0x20 || it.code == 0x7f }
}

private val RESPONSE_HEADERS = mapOf(
    "Cache-Control" to "no-store, max-age=0",
    "X-Content-Type-Options" to "nosniff",
)

private fun documentResponseHeaders(contentSecurityPolicy: String) = RESPONSE_HEADERS + mapOf(
    "Content-Security-Policy" to contentSecurityPolicy,
    "Referrer-Policy" to HtmlPreviewerSecurityPolicy.REFERRER_POLICY,
)

private fun notFound(): WebResourceResponse = WebResourceResponse(
    "text/plain",
    Charsets.UTF_8.name(),
    404,
    "Not Found",
    RESPONSE_HEADERS,
    ByteArrayInputStream("Not Found".toByteArray(Charsets.UTF_8)),
)

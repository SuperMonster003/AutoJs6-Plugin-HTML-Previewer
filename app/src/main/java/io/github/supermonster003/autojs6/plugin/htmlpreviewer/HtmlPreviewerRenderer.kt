package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.net.InetAddress
import java.net.URI
import java.util.Locale

class HtmlPreviewerRenderer {

    fun render(
        html: String,
        themeMode: HtmlPreviewerThemeMode = HtmlPreviewerThemeMode.FOLLOW_SYSTEM,
        truncationNotice: String? = null,
        loadNetworkImages: Boolean = true,
        viewMode: HtmlPreviewerViewMode = HtmlPreviewerViewMode.RENDERED,
        renderTarget: HtmlPreviewerRenderTarget = HtmlPreviewerRenderTarget.SCREEN,
        embeddedResources: Map<String, HtmlPreviewerEmbeddedResource> = emptyMap(),
        allowSiblingResources: Boolean = true,
        interactive: Boolean = false,
    ): String = renderResult(
        html = html,
        themeMode = themeMode,
        truncationNotice = truncationNotice,
        loadNetworkImages = loadNetworkImages,
        viewMode = viewMode,
        renderTarget = renderTarget,
        embeddedResources = embeddedResources,
        allowSiblingResources = allowSiblingResources,
        interactive = interactive,
    ).html

    fun renderResult(
        html: String,
        themeMode: HtmlPreviewerThemeMode = HtmlPreviewerThemeMode.FOLLOW_SYSTEM,
        truncationNotice: String? = null,
        loadNetworkImages: Boolean = true,
        viewMode: HtmlPreviewerViewMode = HtmlPreviewerViewMode.RENDERED,
        renderTarget: HtmlPreviewerRenderTarget = HtmlPreviewerRenderTarget.SCREEN,
        embeddedResources: Map<String, HtmlPreviewerEmbeddedResource> = emptyMap(),
        allowSiblingResources: Boolean = true,
        interactive: Boolean = false,
    ): HtmlPreviewerRenderResult {
        require(
            renderTarget != HtmlPreviewerRenderTarget.PRINT ||
                viewMode == HtmlPreviewerViewMode.RENDERED,
        ) {
            "Print rendering requires the sanitized page mode"
        }
        val allowsScripts = interactive && viewMode == HtmlPreviewerViewMode.RENDERED &&
            renderTarget == HtmlPreviewerRenderTarget.SCREEN
        val effectiveThemeMode = if (renderTarget == HtmlPreviewerRenderTarget.PRINT) {
            HtmlPreviewerThemeMode.LIGHT
        } else {
            themeMode
        }
        val allowsNetworkImages = viewMode == HtmlPreviewerViewMode.RENDERED &&
            loadNetworkImages
        val (document, blockedNetworkResourceCount) = when (viewMode) {
            HtmlPreviewerViewMode.RENDERED -> sanitizeDocument(
                html = html,
                loadNetworkImages = allowsNetworkImages,
                renderTarget = renderTarget,
                interactive = allowsScripts,
            )
            HtmlPreviewerViewMode.SOURCE -> sourceDocument(html) to 0
        }
        document.outputSettings().prettyPrint(false)
        document.selectFirst("html")?.addClass("html-previewer-document")
        if (renderTarget == HtmlPreviewerRenderTarget.PRINT) {
            document.selectFirst("html")?.addClass("html-previewer-print-document")
        }
        document.head().prepend(
            """
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=5">
                <meta name="color-scheme" content="${effectiveThemeMode.colorScheme}">
                <meta name="referrer" content="${HtmlPreviewerSecurityPolicy.REFERRER_POLICY}">
                <meta http-equiv="Content-Security-Policy" content="${HtmlPreviewerSecurityPolicy.contentSecurityPolicy(allowsNetworkImages, allowsScripts)}">
                <link rel="stylesheet" href="${HtmlPreviewerWebOrigin.PREVIEWER_ASSET_BASE_URL}html-base.css">
            """.trimIndent(),
        )
        document.head().appendElement("style")
            .attr("id", "html-previewer-theme")
            .text(
                ":root.html-previewer-document { " +
                    "color-scheme: ${effectiveThemeMode.colorScheme} !important; " +
                    "}",
            )
        if (renderTarget == HtmlPreviewerRenderTarget.PRINT) {
            document.head().appendElement("style")
                .attr("id", "html-previewer-print-theme")
                .text(
                    "@media print { " +
                        ":root.html-previewer-print-document { color-scheme: light !important; } " +
                        ":root.html-previewer-print-document body { " +
                        "background: #ffffff !important; color: #000000 !important; " +
                        "} " +
                        ":root.html-previewer-print-document #html-previewer-truncation-notice { " +
                        "background: #f5f5f5 !important; color: #000000 !important; " +
                        "border-color: #666666 !important; " +
                        "} " +
                        "}",
                )
        }
        truncationNotice
            ?.takeIf(String::isNotBlank)
            ?.let { notice ->
                document.body().appendElement("aside")
                    .attr("id", "html-previewer-truncation-notice")
                    .attr("role", "note")
                    .text(notice)
            }
        return HtmlPreviewerRenderResult(
            html = document.outerHtml(),
            blockedNetworkResourceCount = blockedNetworkResourceCount,
            allowsNetworkImages = allowsNetworkImages,
            viewMode = viewMode,
            renderTarget = renderTarget,
            embeddedResources = if (viewMode == HtmlPreviewerViewMode.RENDERED) {
                embeddedResources
            } else {
                emptyMap()
            },
            interactive = allowsScripts,
            allowSiblingResources = viewMode == HtmlPreviewerViewMode.RENDERED &&
                allowSiblingResources,
        )
    }

    private fun sanitizeDocument(
        html: String,
        loadNetworkImages: Boolean,
        renderTarget: HtmlPreviewerRenderTarget,
        interactive: Boolean,
    ): Pair<Document, Int> {
        val document = Jsoup.parse(html)
        document.select("base, iframe, frame, frameset, object, embed, applet").remove()
        document.select("link[rel]").filter { element ->
            element.attr("rel").lowercase(Locale.ROOT).split(Regex("\\s+"))
                .any { it in setOf("preconnect", "dns-prefetch", "prefetch", "prerender") }
        }.forEach(Element::remove)
        if (!interactive) document.select("script").remove()
        document.select("meta[http-equiv]").remove()
        val blockedNetworkResourceCount = document.allElements.sumOf { element ->
            sanitizeElement(element, loadNetworkImages, renderTarget, interactive)
        }
        return document to blockedNetworkResourceCount
    }

    private fun sourceDocument(source: String): Document =
        Document.createShell(HtmlPreviewerWebOrigin.DOCUMENT_BASE_URL).apply {
            body().addClass("html-previewer-source-view")
            body().appendElement("pre")
                .attr("id", "html-previewer-source")
                .appendElement("code")
                .appendChild(TextNode(source))
        }

    private fun sanitizeElement(
        element: Element,
        loadNetworkImages: Boolean,
        renderTarget: HtmlPreviewerRenderTarget,
        interactive: Boolean,
    ): Int {
        var blockedNetworkResourceCount = 0
        if (!interactive) {
            element.attributes().asList()
                .map { it.key }
                .filter { it.lowercase(Locale.ROOT).startsWith("on") }
                .forEach(element::removeAttr)
        }

        element.removeAttr("ping")
        element.removeAttr("target")
        element.removeAttr("srcdoc")
        element.removeAttr("action")
        element.removeAttr("formaction")
        element.removeAttr("srcset")

        if (element.hasAttr("href")) {
            val href = element.attr("href")
            val linkElement = element.normalName() == "a" || element.normalName() == "area"
            val safe = if (linkElement) {
                HtmlPreviewerUrlPolicy.isSafeLink(href)
            } else {
                HtmlPreviewerUrlPolicy.isSafeResource(href) ||
                    (interactive && loadNetworkImages && HtmlPreviewerUrlPolicy.isSafeRemoteHttpsResource(href))
            }
            if (!safe) {
                element.removeAttr("href")
                if (!linkElement && HtmlPreviewerUrlPolicy.isNetworkResourceReference(href)) {
                    blockedNetworkResourceCount++
                }
            } else if (linkElement) {
                element.attr("rel", "noopener noreferrer")
            }
        }

        listOf("src", "poster", "background", "xlink:href").forEach { attribute ->
            if (element.hasAttr(attribute)) {
                val value = element.attr(attribute)
                val isImageSource = attribute == "src" && element.normalName() == "img"
                val isDisabledNetworkImage = isImageSource &&
                    !loadNetworkImages &&
                    HtmlPreviewerUrlPolicy.isSafeRemoteHttpsResource(value)
                val safe = if (isImageSource) {
                    HtmlPreviewerUrlPolicy.isSafeImageResource(value)
                } else {
                    HtmlPreviewerUrlPolicy.isSafeResource(value) ||
                        (interactive && loadNetworkImages && HtmlPreviewerUrlPolicy.isSafeRemoteHttpsResource(value))
                }
                if (!safe || isDisabledNetworkImage) {
                    element.removeAttr(attribute)
                    if (HtmlPreviewerUrlPolicy.isNetworkResourceReference(value)) {
                        blockedNetworkResourceCount++
                    }
                }
            }
        }

        configureImage(element, renderTarget)
        return blockedNetworkResourceCount
    }

    private fun configureImage(
        element: Element,
        renderTarget: HtmlPreviewerRenderTarget,
    ) {
        if (element.normalName() != "img" || !element.hasAttr("src")) return

        if (renderTarget == HtmlPreviewerRenderTarget.PRINT) {
            element.attr("loading", "eager")
            element.attr("decoding", "sync")
        } else if (HtmlPreviewerUrlPolicy.isSafeRemoteHttpsResource(element.attr("src"))) {
            element.attr("loading", "lazy")
            element.attr("decoding", "async")
        }
        if (HtmlPreviewerUrlPolicy.isSafeRemoteHttpsResource(element.attr("src"))) {
            element.attr("referrerpolicy", HtmlPreviewerSecurityPolicy.REFERRER_POLICY)
        }
    }
}

data class HtmlPreviewerRenderResult(
    val html: String,
    val blockedNetworkResourceCount: Int,
    val allowsNetworkImages: Boolean,
    val viewMode: HtmlPreviewerViewMode,
    val renderTarget: HtmlPreviewerRenderTarget,
    val embeddedResources: Map<String, HtmlPreviewerEmbeddedResource>,
    val allowSiblingResources: Boolean,
    val interactive: Boolean = false,
)

internal object HtmlPreviewerSecurityPolicy {

    const val REFERRER_POLICY = "no-referrer"

    fun contentSecurityPolicy(loadNetworkImages: Boolean, interactive: Boolean = false): String {
        if (interactive) {
            val network = if (loadNetworkImages) " https:" else ""
            return "default-src 'none'; style-src 'self' 'unsafe-inline'$network; " +
                "script-src 'self' 'unsafe-inline' 'unsafe-eval'$network; " +
                "img-src 'self' data: blob:$network; font-src 'self' data:$network; " +
                "media-src 'self' data: blob:$network; connect-src 'none'; " +
                "frame-src 'none'; child-src 'none'; object-src 'none'; worker-src 'none'; " +
                "manifest-src 'none'; base-uri 'none'; form-action 'none'; webrtc 'block'"
        }
        return "default-src 'none'; style-src 'self' 'unsafe-inline'; " +
            "img-src 'self' data:${if (loadNetworkImages) " https:" else ""}; " +
            "font-src 'self' data:; media-src 'self' data:; script-src 'none'; " +
            "connect-src 'none'; frame-src 'none'; child-src 'none'; object-src 'none'; " +
            "worker-src 'none'; manifest-src 'none'; base-uri 'none'; form-action 'none'"
    }
}

internal object HtmlPreviewerUrlPolicy {

    private val schemePattern = Regex("""^([a-z][a-z0-9+.-]*):""", RegexOption.IGNORE_CASE)
    private val safeDataImagePattern = Regex(
        """^data:image/(?:png|jpeg|gif|webp|avif|bmp|svg\+xml)(?:;|,)""",
        RegexOption.IGNORE_CASE,
    )
    private val safeDataResourcePattern = Regex(
        """^data:(?:image/(?:png|jpeg|gif|webp|avif|bmp|svg\+xml)|audio/(?:mpeg|ogg|wav|mp4)|video/(?:mp4|webm|ogg))(?:;|,)""",
        RegexOption.IGNORE_CASE,
    )

    fun isSafeLink(value: String): Boolean {
        val normalized = normalize(value) ?: return false
        if (normalized.startsWith("//") || normalized.startsWith("\\\\")) return false
        val scheme = schemePattern.find(normalized)?.groupValues?.get(1)?.lowercase(Locale.ROOT)
        return scheme == null || scheme == "http" || scheme == "https"
    }

    fun isSafeResource(value: String): Boolean {
        val normalized = normalize(value) ?: return false
        if (normalized.isEmpty()) return false
        if (normalized.startsWith("//") || normalized.startsWith("\\\\")) return false
        val scheme = schemePattern.find(normalized)?.groupValues?.get(1)?.lowercase(Locale.ROOT)
        return when {
            scheme == null -> true
            scheme == "data" -> safeDataResourcePattern.containsMatchIn(normalized)
            else -> false
        }
    }

    fun isSafeImageResource(value: String): Boolean {
        val normalized = normalize(value) ?: return false
        if (normalized.isEmpty()) return false
        if (normalized.startsWith("//") || normalized.startsWith("\\\\")) return false
        val scheme = schemePattern.find(normalized)?.groupValues?.get(1)?.lowercase(Locale.ROOT)
        return when {
            scheme == null -> true
            scheme == "data" -> safeDataImagePattern.containsMatchIn(normalized)
            scheme == "https" -> isSafeRemoteHttpsResource(normalized)
            else -> false
        }
    }

    fun isSafeRemoteHttpsResource(value: String): Boolean = safeRemoteHttpsHost(value) != null

    fun isNetworkResourceReference(value: String): Boolean {
        val normalized = normalize(value) ?: return false
        if (normalized.startsWith("//")) return true
        val scheme = schemePattern.find(normalized)?.groupValues?.get(1)?.lowercase(Locale.ROOT)
        return scheme == "http" || scheme == "https"
    }

    fun safeRemoteHttpsHost(value: String): String? {
        val normalized = normalize(value) ?: return null
        val uri = remoteHttpsOrigin(normalized) ?: return null
        if (uri.rawUserInfo != null) return null
        val host = uri.host?.trimEnd('.')?.lowercase(Locale.ROOT) ?: return null
        if (host.isEmpty() || host == "localhost" || host.endsWith(".localhost")) return null
        return host.takeIf(::isSafeRemoteHost)
    }

    private fun remoteHttpsOrigin(value: String): URI? {
        val schemeSeparator = value.indexOf("://")
        if (schemeSeparator < 0 || !value.substring(0, schemeSeparator).equals("https", ignoreCase = true)) {
            return null
        }
        val authorityStart = schemeSeparator + 3
        val authorityEnd = value.indexOfAny(
            chars = charArrayOf('/', '?', '#'),
            startIndex = authorityStart,
        ).takeIf { it >= 0 } ?: value.length
        val authority = value.substring(authorityStart, authorityEnd)
        if (authority.isEmpty() || authority.any { it.isWhitespace() || it == '\\' }) return null
        return runCatching { URI("https://$authority") }.getOrNull()
    }

    private fun isSafeRemoteHost(host: String): Boolean {
        val unwrappedHost = host.removePrefix("[").removeSuffix("]")
        if (unwrappedHost.contains(':')) {
            val address = runCatching { InetAddress.getByName(unwrappedHost) }.getOrNull() ?: return false
            val bytes = address.address
            val first = bytes.firstOrNull()?.toInt()?.and(0xff) ?: return false
            if (
                address.isAnyLocalAddress ||
                address.isLoopbackAddress ||
                address.isLinkLocalAddress ||
                address.isSiteLocalAddress ||
                address.isMulticastAddress ||
                first and 0xfe == 0xfc
            ) {
                return false
            }
            return true
        }

        if (unwrappedHost.all(Char::isDigit) || unwrappedHost.startsWith("0x", ignoreCase = true)) {
            return false
        }
        if (unwrappedHost.any { it != '.' && !it.isDigit() }) return true
        val parts = unwrappedHost.split('.')
        if (
            parts.size != 4 ||
            parts.any { it.isEmpty() || it.length > 1 && it.startsWith('0') }
        ) {
            return false
        }
        val octets = parts.map { it.toIntOrNull() ?: return false }
        if (octets.any { it !in 0..255 }) return false
        val first = octets[0]
        val second = octets[1]
        return when {
            first == 0 || first == 10 || first == 127 || first >= 224 -> false
            first == 100 && second in 64..127 -> false
            first == 169 && second == 254 -> false
            first == 172 && second in 16..31 -> false
            first == 192 && second == 168 -> false
            first == 198 && second in 18..19 -> false
            else -> true
        }
    }

    private fun normalize(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isEmpty()) return ""
        if (trimmed.any { it == '\u0000' || it.code < 0x20 || it.code == 0x7f }) return null
        return trimmed
    }
}

internal object HtmlPreviewerWebOrigin {
    const val DOMAIN = "appassets.androidplatform.net"
    const val DOCUMENT_PATH_PREFIX = "/html-document/"
    const val DOCUMENT_BASE_URL = "https://$DOMAIN$DOCUMENT_PATH_PREFIX"
    const val DOCUMENT_FILE_NAME = "__previewer__.html"
    const val DOCUMENT_URL = "$DOCUMENT_BASE_URL$DOCUMENT_FILE_NAME"
    const val PREVIEWER_ASSET_PATH_PREFIX = "/html-previewer-assets/"
    const val PREVIEWER_ASSET_BASE_URL = "https://$DOMAIN$PREVIEWER_ASSET_PATH_PREFIX"

    fun isDomain(host: String?): Boolean =
        host?.trimEnd('.')?.equals(DOMAIN, ignoreCase = true) == true
}

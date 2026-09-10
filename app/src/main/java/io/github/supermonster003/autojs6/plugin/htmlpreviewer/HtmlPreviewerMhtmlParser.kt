package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.jsoup.Jsoup
import org.jsoup.nodes.DataNode
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Locale

/**
 * A deliberately bounded MIME multipart/related reader for MHTML renders.
 *
 * Content-Location and Content-ID values never become authorities or filesystem paths. They are
 * used only to replace matching archive references with opaque paths on the previewer's virtual
 * HTTPS origin.
 */
internal object HtmlPreviewerMhtmlParser {

    data class Limits(
        val maxPartCount: Int = 256,
        val maxDepth: Int = 8,
        val maxHeaderBytesPerEntity: Int = 64 * 1024,
        val maxTotalHeaderBytes: Int = 512 * 1024,
        val maxDecodedBytes: Int = 8 * 1024 * 1024,
    ) {
        init {
            require(maxPartCount > 0)
            require(maxDepth >= 0)
            require(maxHeaderBytesPerEntity > 0)
            require(maxTotalHeaderBytes >= maxHeaderBytesPerEntity)
            require(maxDecodedBytes > 0)
        }
    }

    fun parse(
        bytes: ByteArray,
        limits: Limits = Limits(),
    ): HtmlPreviewerMhtmlDocument = Parser(bytes, limits).parse()

    private class Parser(
        private val bytes: ByteArray,
        private val limits: Limits,
    ) {

        private var entityCount = 0
        private var totalHeaderBytes = 0
        private var totalDecodedBytes = 0
        private var atomicIndex = 0

        fun parse(): HtmlPreviewerMhtmlDocument {
            if (bytes.isEmpty()) fail("The MHTML archive is empty")
            val archive = parseEntity(0, bytes.size, depth = 0)
            if (archive.contentType.mediaType != MULTIPART_RELATED) {
                fail("The MHTML root must be multipart/related")
            }
            val rootEntity = selectHtmlRoot(archive)
                ?: fail("The MHTML archive has no HTML root part")

            val initialBase = absoluteHeaderUri(archive.headers[SNAPSHOT_CONTENT_LOCATION])
                ?: SYNTHETIC_MESSAGE_BASE
            val parts = mutableListOf<AtomicPart>()
            collectAtomicParts(archive, initialBase, parts)
            val rootPart = parts.firstOrNull { it.entity === rootEntity }
                ?: fail("The selected HTML root is not an atomic MIME part")

            val virtualPaths = linkedMapOf<Int, String>()
            parts.forEach { part ->
                if (part !== rootPart) {
                    HtmlPreviewerMhtmlResourcePolicy.extension(part.mediaType)?.let { extension ->
                        virtualPaths[part.index] = "$RESOURCE_PATH_PREFIX" +
                            "resource-${part.index.toString().padStart(4, '0')}.$extension"
                    }
                }
            }

            val aliasOwners = linkedMapOf<String, MutableSet<Int>>()
            parts.forEach { part ->
                part.aliases.forEach { alias ->
                    aliasOwners.getOrPut(alias, ::linkedSetOf).add(part.index)
                }
            }
            val aliasPaths = linkedMapOf<String, String>()
            aliasOwners.forEach { (alias, owners) ->
                owners.singleOrNull()?.let { owner ->
                    virtualPaths[owner]?.let { path -> aliasPaths[alias] = path }
                }
            }

            val resources = linkedMapOf<String, HtmlPreviewerEmbeddedResource>()
            parts.forEach { part ->
                val path = virtualPaths[part.index] ?: return@forEach
                val resource = if (part.mediaType == TEXT_CSS) {
                    val css = HtmlPreviewerTextCodec.decode(
                        bytes = part.decodedBody,
                        declaredCharset = part.charset,
                    )
                    HtmlPreviewerEmbeddedResource(
                        bytes = rewriteCss(css, part.referenceBase, aliasPaths)
                            .toByteArray(StandardCharsets.UTF_8),
                        mimeType = TEXT_CSS,
                        charset = StandardCharsets.UTF_8.name(),
                    )
                } else {
                    HtmlPreviewerEmbeddedResource(
                        bytes = part.decodedBody,
                        mimeType = part.mediaType,
                    )
                }
                resources[path] = resource
            }

            val source = HtmlPreviewerTextCodec.decode(
                bytes = rootPart.decodedBody,
                declaredCharset = rootPart.charset,
            )
            return HtmlPreviewerMhtmlDocument(
                sourceText = source,
                previewerText = rewriteHtml(source, rootPart.referenceBase, aliasPaths),
                embeddedResources = resources,
            )
        }

        private fun parseEntity(
            start: Int,
            end: Int,
            depth: Int,
        ): MimeEntity {
            if (depth > limits.maxDepth) fail("MIME nesting exceeds ${limits.maxDepth}")
            entityCount++
            if (entityCount > limits.maxPartCount) {
                fail("MIME part count exceeds ${limits.maxPartCount}")
            }

            val parsedHeaders = parseHeaders(start, end)
            val contentType = parseContentType(parsedHeaders.values[CONTENT_TYPE])
            val transferEncoding = parsedHeaders.values[CONTENT_TRANSFER_ENCODING]
                ?.trim()
                ?.lowercase(Locale.ROOT)
                .orEmpty()
            val children = if (contentType.mediaType.startsWith("multipart/")) {
                if (transferEncoding !in IDENTITY_TRANSFER_ENCODINGS) {
                    fail("Multipart containers cannot use $transferEncoding transfer encoding")
                }
                val boundary = contentType.parameters[BOUNDARY]
                    ?.takeIf(::isValidBoundary)
                    ?: fail("Multipart entity has no valid boundary")
                splitMultipart(parsedHeaders.bodyStart, end, boundary).map { range ->
                    parseEntity(range.first, range.lastExclusive, depth + 1)
                }
            } else {
                emptyList()
            }
            return MimeEntity(
                headers = parsedHeaders.values,
                contentType = contentType,
                transferEncoding = transferEncoding,
                bodyStart = parsedHeaders.bodyStart,
                bodyEnd = end,
                children = children,
            )
        }

        private fun parseHeaders(start: Int, end: Int): ParsedHeaders {
            val entries = mutableListOf<Pair<String, StringBuilder>>()
            var position = start
            var headerBytes = 0
            while (position < end) {
                val line = readLine(position, end)
                headerBytes += line.next - position
                if (headerBytes > limits.maxHeaderBytesPerEntity) {
                    fail("MIME entity headers exceed ${limits.maxHeaderBytesPerEntity} bytes")
                }
                position = line.next
                if (line.contentEnd == line.start) {
                    totalHeaderBytes += headerBytes
                    if (totalHeaderBytes > limits.maxTotalHeaderBytes) {
                        fail("MIME headers exceed ${limits.maxTotalHeaderBytes} bytes")
                    }
                    val values = linkedMapOf<String, String>()
                    entries.forEach { (name, value) ->
                        if (name in SINGLETON_HEADERS && name in values) {
                            fail("Duplicate $name header")
                        }
                        values.putIfAbsent(name, value.toString())
                    }
                    return ParsedHeaders(values, position)
                }

                val lineText = String(
                    bytes,
                    line.start,
                    line.contentEnd - line.start,
                    StandardCharsets.ISO_8859_1,
                )
                if (lineText.firstOrNull() == ' ' || lineText.firstOrNull() == '\t') {
                    val previous = entries.lastOrNull()
                        ?: fail("MIME header continuation has no preceding field")
                    previous.second.append(' ').append(lineText.trimStart(' ', '\t'))
                    continue
                }
                val separator = lineText.indexOf(':')
                if (separator <= 0) fail("Malformed MIME header")
                val name = lineText.substring(0, separator)
                if (!name.all(::isHeaderNameCharacter)) fail("Malformed MIME header name")
                entries += name.lowercase(Locale.ROOT) to
                    StringBuilder(lineText.substring(separator + 1).trim(' ', '\t'))
            }
            fail("MIME entity headers are not terminated")
        }

        private fun splitMultipart(
            start: Int,
            end: Int,
            boundary: String,
        ): List<ByteRange> {
            val delimiter = "--$boundary".toByteArray(StandardCharsets.ISO_8859_1)
            val ranges = mutableListOf<ByteRange>()
            var position = start
            var currentPartStart: Int? = null
            var sawOpeningBoundary = false
            while (position < end) {
                val line = readLine(position, end)
                when (boundaryLineKind(line, delimiter)) {
                    BoundaryLineKind.OPEN -> {
                        currentPartStart?.let { partStart ->
                            ranges += ByteRange(partStart, contentEndBeforeBoundary(line.start, partStart))
                        }
                        sawOpeningBoundary = true
                        currentPartStart = line.next
                    }

                    BoundaryLineKind.CLOSE -> {
                        if (!sawOpeningBoundary) fail("Closing MIME boundary precedes an opening boundary")
                        currentPartStart?.let { partStart ->
                            ranges += ByteRange(partStart, contentEndBeforeBoundary(line.start, partStart))
                        }
                        if (ranges.isEmpty()) fail("Multipart entity contains no body parts")
                        return ranges
                    }

                    null -> Unit
                }
                position = line.next
            }
            fail("Multipart entity has no closing boundary")
        }

        private fun boundaryLineKind(
            line: Line,
            delimiter: ByteArray,
        ): BoundaryLineKind? {
            val length = line.contentEnd - line.start
            if (length < delimiter.size) return null
            delimiter.indices.forEach { offset ->
                if (bytes[line.start + offset] != delimiter[offset]) return null
            }
            var position = line.start + delimiter.size
            var closing = false
            if (
                position + 1 < line.contentEnd &&
                bytes[position] == '-'.code.toByte() &&
                bytes[position + 1] == '-'.code.toByte()
            ) {
                closing = true
                position += 2
            }
            while (position < line.contentEnd && (bytes[position] == ' '.code.toByte() || bytes[position] == '\t'.code.toByte())) {
                position++
            }
            if (position != line.contentEnd) return null
            return if (closing) BoundaryLineKind.CLOSE else BoundaryLineKind.OPEN
        }

        private fun contentEndBeforeBoundary(
            boundaryStart: Int,
            partStart: Int,
        ): Int {
            var end = boundaryStart
            if (end > partStart && bytes[end - 1] == '\n'.code.toByte()) {
                end--
                if (end > partStart && bytes[end - 1] == '\r'.code.toByte()) end--
            }
            return end
        }

        private fun readLine(start: Int, end: Int): Line {
            var newline = start
            while (newline < end && bytes[newline] != '\n'.code.toByte()) newline++
            val contentEnd = if (newline > start && bytes[newline - 1] == '\r'.code.toByte()) {
                newline - 1
            } else {
                newline
            }
            return Line(
                start = start,
                contentEnd = contentEnd,
                next = if (newline < end) newline + 1 else end,
            )
        }

        private fun selectHtmlRoot(entity: MimeEntity): MimeEntity? = when {
            entity.contentType.mediaType == MULTIPART_RELATED -> {
                val selected = entity.contentType.parameters[START]?.let { requestedId ->
                    val normalized = normalizeContentId(requestedId)
                        ?: fail("multipart/related has an invalid start parameter")
                    entity.children.singleOrNull { child ->
                        normalizeContentId(child.headers[CONTENT_ID]) == normalized
                    } ?: fail("multipart/related start does not identify one direct child")
                } ?: entity.children.firstOrNull()
                selected?.let(::selectHtmlRoot)
            }

            entity.contentType.mediaType == MULTIPART_ALTERNATIVE ->
                entity.children.asReversed().firstNotNullOfOrNull(::selectHtmlRoot)

            entity.children.isNotEmpty() -> null
            entity.contentType.mediaType in HTML_MEDIA_TYPES -> entity
            else -> null
        }

        private fun collectAtomicParts(
            entity: MimeEntity,
            inheritedBase: URI,
            output: MutableList<AtomicPart>,
        ) {
            val location = resolveHeaderUri(entity.headers[CONTENT_LOCATION], inheritedBase)
            val referenceBase = resolveHeaderUri(entity.headers[CONTENT_BASE], inheritedBase)
                ?: location
                ?: inheritedBase
            if (entity.children.isNotEmpty()) {
                entity.children.forEach { child -> collectAtomicParts(child, referenceBase, output) }
                return
            }

            val decoded = decodeBody(entity)
            val index = atomicIndex++
            val aliases = linkedSetOf<String>()
            normalizeContentId(entity.headers[CONTENT_ID])?.let { aliases += cidAlias(it) }
            location?.let { aliases += locationAlias(it) }
            output += AtomicPart(
                index = index,
                entity = entity,
                decodedBody = decoded,
                mediaType = entity.contentType.mediaType,
                charset = entity.contentType.parameters[CHARSET],
                referenceBase = referenceBase,
                aliases = aliases,
            )
        }

        private fun decodeBody(entity: MimeEntity): ByteArray {
            val encoded = bytes.copyOfRange(entity.bodyStart, entity.bodyEnd)
            val decoded = when (entity.transferEncoding) {
                in IDENTITY_TRANSFER_ENCODINGS -> encoded
                BASE64 -> decodeBase64(encoded)
                QUOTED_PRINTABLE -> decodeQuotedPrintable(encoded)
                else -> fail("Unsupported MIME transfer encoding: ${entity.transferEncoding}")
            }
            totalDecodedBytes += decoded.size
            if (totalDecodedBytes > limits.maxDecodedBytes) {
                fail("Decoded MIME bodies exceed ${limits.maxDecodedBytes} bytes")
            }
            return decoded
        }

        private fun decodeBase64(encoded: ByteArray): ByteArray {
            val clean = encoded.filterNot(::isMimeWhitespace).toByteArray()
            if (clean.isEmpty()) return ByteArray(0)
            if (clean.size % 4 != 0) fail("Malformed Base64 MIME body")
            val output = ByteArrayOutputStream(clean.size / 4 * 3)
            var offset = 0
            while (offset < clean.size) {
                val lastGroup = offset + 4 == clean.size
                val first = base64Value(clean[offset])
                val second = base64Value(clean[offset + 1])
                if (first < 0 || second < 0) fail("Malformed Base64 MIME body")
                val thirdByte = clean[offset + 2]
                val fourthByte = clean[offset + 3]
                val thirdPadding = thirdByte == '='.code.toByte()
                val fourthPadding = fourthByte == '='.code.toByte()
                if ((thirdPadding || fourthPadding) && !lastGroup) fail("Malformed Base64 padding")
                if (thirdPadding && !fourthPadding) fail("Malformed Base64 padding")
                val third = if (thirdPadding) 0 else base64Value(thirdByte)
                val fourth = if (fourthPadding) 0 else base64Value(fourthByte)
                if (third < 0 || fourth < 0) fail("Malformed Base64 MIME body")

                output.write(first shl 2 or (second ushr 4))
                if (!thirdPadding) output.write((second and 0x0f) shl 4 or (third ushr 2))
                if (!fourthPadding) output.write((third and 0x03) shl 6 or fourth)
                offset += 4
            }
            return output.toByteArray()
        }

        private fun decodeQuotedPrintable(encoded: ByteArray): ByteArray {
            val output = ByteArrayOutputStream(encoded.size)
            var position = 0
            while (position < encoded.size) {
                val value = encoded[position]
                if (value != '='.code.toByte()) {
                    output.write(value.toInt() and 0xff)
                    position++
                    continue
                }
                when {
                    position + 2 < encoded.size &&
                        encoded[position + 1] == '\r'.code.toByte() &&
                        encoded[position + 2] == '\n'.code.toByte() -> position += 3

                    position + 1 < encoded.size && encoded[position + 1] == '\n'.code.toByte() ->
                        position += 2

                    position + 2 < encoded.size -> {
                        val high = hexValue(encoded[position + 1])
                        val low = hexValue(encoded[position + 2])
                        if (high < 0 || low < 0) fail("Malformed quoted-printable MIME body")
                        output.write(high shl 4 or low)
                        position += 3
                    }

                    else -> fail("Malformed quoted-printable MIME body")
                }
            }
            return output.toByteArray()
        }

        private fun parseContentType(value: String?): ContentType {
            if (value == null) return ContentType(TEXT_PLAIN, emptyMap())
            var position = 0
            while (value.getOrNull(position)?.isWhitespace() == true) position++
            val mediaStart = position
            while (position < value.length && value[position] != ';') position++
            val mediaType = value.substring(mediaStart, position)
                .trim()
                .lowercase(Locale.ROOT)
            if (!MEDIA_TYPE_PATTERN.matches(mediaType)) fail("Malformed MIME Content-Type")

            val parameters = linkedMapOf<String, String>()
            while (position < value.length) {
                if (value[position] != ';') fail("Malformed MIME Content-Type parameter")
                position++
                while (value.getOrNull(position)?.isWhitespace() == true) position++
                if (position >= value.length) break
                val nameStart = position
                while (value.getOrNull(position)?.let(::isParameterNameCharacter) == true) position++
                if (position == nameStart) fail("Malformed MIME Content-Type parameter name")
                val name = value.substring(nameStart, position).lowercase(Locale.ROOT)
                while (value.getOrNull(position)?.isWhitespace() == true) position++
                if (value.getOrNull(position) != '=') fail("Malformed MIME Content-Type parameter")
                position++
                while (value.getOrNull(position)?.isWhitespace() == true) position++
                val parameterValue = if (value.getOrNull(position) == '"') {
                    position++
                    buildString {
                        var closed = false
                        while (position < value.length) {
                            when (val character = value[position++]) {
                                '\\' -> {
                                    if (position >= value.length) fail("Malformed quoted MIME parameter")
                                    append(value[position++])
                                }
                                '"' -> {
                                    closed = true
                                    break
                                }
                                '\r', '\n' -> fail("Malformed quoted MIME parameter")
                                else -> append(character)
                            }
                        }
                        if (!closed) fail("Unterminated quoted MIME parameter")
                    }
                } else {
                    val parameterStart = position
                    while (position < value.length && value[position] != ';') position++
                    value.substring(parameterStart, position).trim()
                        .takeIf(String::isNotEmpty)
                        ?: fail("Empty MIME parameter")
                }
                parameters.putIfAbsent(name, parameterValue)
                while (value.getOrNull(position)?.isWhitespace() == true) position++
            }
            return ContentType(mediaType, parameters)
        }

        private fun rewriteHtml(
            html: String,
            inheritedBase: URI,
            aliases: Map<String, String>,
        ): String {
            val document = Jsoup.parse(html)
            val documentBase = document.selectFirst("base[href]")
                ?.attr("href")
                ?.let { resolveReferenceUri(it, inheritedBase) }
                ?: inheritedBase
            document.select("base").remove()
            document.allElements.forEach { element ->
                listOf("src", "poster", "background", "xlink:href").forEach { attribute ->
                    if (element.hasAttr(attribute)) {
                        rewriteReference(element.attr(attribute), documentBase, aliases)
                            ?.let { element.attr(attribute, it) }
                    }
                }
                if (
                    element.hasAttr("href") &&
                    element.normalName() != "a" &&
                    element.normalName() != "area"
                ) {
                    rewriteReference(element.attr("href"), documentBase, aliases)
                        ?.let { element.attr("href", it) }
                }
                if (element.hasAttr("style")) {
                    element.attr("style", rewriteCss(element.attr("style"), documentBase, aliases))
                }
                if (element.normalName() == "style") {
                    element.childNodes().filterIsInstance<DataNode>().forEach { node ->
                        node.setWholeData(rewriteCss(node.wholeData, documentBase, aliases))
                    }
                }
            }
            document.outputSettings().prettyPrint(false)
            return document.outerHtml()
        }

        private fun rewriteCss(
            css: String,
            base: URI,
            aliases: Map<String, String>,
        ): String {
            var result = CSS_URL_PATTERN.replace(css) { match ->
                val quote = match.groupValues[1]
                val raw = if (quote.isNotEmpty()) match.groupValues[2] else match.groupValues[3].trim()
                val rewritten = rewriteReference(raw, base, aliases) ?: return@replace match.value
                if (quote.isNotEmpty()) "url($quote$rewritten$quote)" else "url($rewritten)"
            }
            result = CSS_DOUBLE_QUOTED_IMPORT_PATTERN.replace(result) { match ->
                val rewritten = rewriteReference(match.groupValues[2], base, aliases)
                    ?: return@replace match.value
                "${match.groupValues[1]}\"$rewritten\""
            }
            return CSS_SINGLE_QUOTED_IMPORT_PATTERN.replace(result) { match ->
                val rewritten = rewriteReference(match.groupValues[2], base, aliases)
                    ?: return@replace match.value
                "${match.groupValues[1]}'$rewritten'"
            }
        }

        private fun rewriteReference(
            value: String,
            base: URI,
            aliases: Map<String, String>,
        ): String? {
            val normalized = value.trim()
            if (normalized.isEmpty() || normalized.startsWith('#')) return null
            val fragment: String?
            val alias = if (normalized.startsWith("cid:", ignoreCase = true)) {
                fragment = null
                val contentId = percentDecodeContentId(normalized.substring(4)) ?: return null
                cidAlias(contentId)
            } else {
                val uri = resolveReferenceUri(normalized, base) ?: return null
                fragment = uri.rawFragment
                locationAlias(uri)
            }
            val path = aliases[alias] ?: return null
            return buildString {
                append(HtmlPreviewerWebOrigin.DOCUMENT_PATH_PREFIX)
                append(path)
                fragment?.takeIf(String::isNotEmpty)?.let { append('#').append(it) }
            }
        }

        private fun resolveReferenceUri(value: String, base: URI): URI? {
            if (value.any(::isForbiddenUriCharacter)) return null
            return runCatching { base.resolve(URI(value)) }
                .getOrNull()
                ?.takeIf { it.isAbsolute }
        }

        private fun resolveHeaderUri(value: String?, base: URI): URI? {
            val normalized = value
                ?.trim()
                ?.removeSurrounding("<", ">")
                ?.removeSurrounding("\"")
                ?.takeIf(String::isNotEmpty)
                ?: return null
            return resolveReferenceUri(normalized, base)
        }

        private fun absoluteHeaderUri(value: String?): URI? {
            val normalized = value
                ?.trim()
                ?.removeSurrounding("<", ">")
                ?.removeSurrounding("\"")
                ?.takeIf(String::isNotEmpty)
                ?: return null
            if (normalized.any(::isForbiddenUriCharacter)) return null
            return runCatching { URI(normalized) }.getOrNull()?.takeIf(URI::isAbsolute)
        }

        private fun locationAlias(uri: URI): String {
            val raw = uri.toASCIIString().substringBefore('#')
            val separator = raw.indexOf(':')
            val normalized = if (separator > 0) {
                raw.substring(0, separator).lowercase(Locale.ROOT) + raw.substring(separator)
            } else {
                raw
            }
            return "location:$normalized"
        }

        private fun cidAlias(contentId: String): String = "cid:$contentId"

        private fun normalizeContentId(value: String?): String? {
            val normalized = value
                ?.trim()
                ?.removeSurrounding("<", ">")
                ?.takeIf(String::isNotEmpty)
                ?: return null
            return normalized.takeIf { id ->
                id.none { it.isWhitespace() || it == '<' || it == '>' || isForbiddenUriCharacter(it) }
            }
        }

        private fun percentDecodeContentId(value: String): String? {
            if (value.isEmpty()) return null
            val output = StringBuilder(value.length)
            var position = 0
            while (position < value.length) {
                val character = value[position]
                if (character == '%') {
                    if (position + 2 >= value.length) return null
                    val high = value[position + 1].digitToIntOrNull(16) ?: return null
                    val low = value[position + 2].digitToIntOrNull(16) ?: return null
                    output.append((high shl 4 or low).toChar())
                    position += 3
                } else {
                    output.append(character)
                    position++
                }
            }
            return normalizeContentId(output.toString())
        }

        private fun isValidBoundary(boundary: String): Boolean =
            boundary.isNotEmpty() &&
                boundary.length <= MAX_BOUNDARY_LENGTH &&
                boundary.last() != ' ' &&
                boundary.all { it.code in 0x20..0x7e && it != '\r' && it != '\n' }

        private fun base64Value(value: Byte): Int = when (val character = value.toInt() and 0xff) {
            in 'A'.code..'Z'.code -> character - 'A'.code
            in 'a'.code..'z'.code -> character - 'a'.code + 26
            in '0'.code..'9'.code -> character - '0'.code + 52
            '+'.code -> 62
            '/'.code -> 63
            else -> -1
        }

        private fun hexValue(value: Byte): Int = when (val character = value.toInt() and 0xff) {
            in '0'.code..'9'.code -> character - '0'.code
            in 'A'.code..'F'.code -> character - 'A'.code + 10
            in 'a'.code..'f'.code -> character - 'a'.code + 10
            else -> -1
        }

        private fun fail(message: String): Nothing = throw HtmlPreviewerMhtmlException(message)
    }

    private data class MimeEntity(
        val headers: Map<String, String>,
        val contentType: ContentType,
        val transferEncoding: String,
        val bodyStart: Int,
        val bodyEnd: Int,
        val children: List<MimeEntity>,
    )

    private data class ContentType(
        val mediaType: String,
        val parameters: Map<String, String>,
    )

    private data class ParsedHeaders(
        val values: Map<String, String>,
        val bodyStart: Int,
    )

    private data class Line(
        val start: Int,
        val contentEnd: Int,
        val next: Int,
    )

    private data class ByteRange(
        val first: Int,
        val lastExclusive: Int,
    )

    private data class AtomicPart(
        val index: Int,
        val entity: MimeEntity,
        val decodedBody: ByteArray,
        val mediaType: String,
        val charset: String?,
        val referenceBase: URI,
        val aliases: Set<String>,
    )

    private enum class BoundaryLineKind { OPEN, CLOSE }

    private const val BASE64 = "base64"
    private const val BOUNDARY = "boundary"
    private const val CHARSET = "charset"
    private const val CONTENT_BASE = "content-base"
    private const val CONTENT_ID = "content-id"
    private const val CONTENT_LOCATION = "content-location"
    private const val CONTENT_TRANSFER_ENCODING = "content-transfer-encoding"
    private const val CONTENT_TYPE = "content-type"
    private const val MAX_BOUNDARY_LENGTH = 200
    private const val MULTIPART_ALTERNATIVE = "multipart/alternative"
    private const val MULTIPART_RELATED = "multipart/related"
    private const val QUOTED_PRINTABLE = "quoted-printable"
    private const val RESOURCE_PATH_PREFIX = "__mhtml__/"
    private const val SNAPSHOT_CONTENT_LOCATION = "snapshot-content-location"
    private const val START = "start"
    private const val TEXT_CSS = "text/css"
    private const val TEXT_PLAIN = "text/plain"

    private val SYNTHETIC_MESSAGE_BASE = URI("thismessage:/")
    private val HTML_MEDIA_TYPES = setOf("text/html", "application/xhtml+xml")
    private val IDENTITY_TRANSFER_ENCODINGS = setOf("", "7bit", "8bit", "binary")
    private val SINGLETON_HEADERS = setOf(
        CONTENT_BASE,
        CONTENT_ID,
        CONTENT_LOCATION,
        CONTENT_TRANSFER_ENCODING,
        CONTENT_TYPE,
    )
    private val MEDIA_TYPE_PATTERN = Regex("^[a-z0-9!#$&^_.+-]+/[a-z0-9!#$&^_.+-]+$")
    private val CSS_URL_PATTERN = Regex(
        """url\(\s*(?:(['\"])(.*?)\1|([^)]*?))\s*\)""",
        setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL),
    )
    private val CSS_DOUBLE_QUOTED_IMPORT_PATTERN = Regex(
        """(@import\s+)\"([^\"]+)\"""",
        RegexOption.IGNORE_CASE,
    )
    private val CSS_SINGLE_QUOTED_IMPORT_PATTERN = Regex(
        """(@import\s+)'([^']+)'""",
        RegexOption.IGNORE_CASE,
    )

    private fun isHeaderNameCharacter(character: Char): Boolean =
        character in 'A'..'Z' ||
            character in 'a'..'z' ||
            character in '0'..'9' ||
            character in "!#$%&'*+-.^_`|~"

    private fun isParameterNameCharacter(character: Char): Boolean =
        isHeaderNameCharacter(character)

    private fun isMimeWhitespace(value: Byte): Boolean = when (value.toInt() and 0xff) {
        ' '.code, '\t'.code, '\r'.code, '\n'.code -> true
        else -> false
    }

    private fun isForbiddenUriCharacter(character: Char): Boolean =
        character == '\u0000' || character.code < 0x20 || character.code == 0x7f
}

internal object HtmlPreviewerMhtmlResourcePolicy {

    fun extension(mediaType: String): String? = when (mediaType.lowercase(Locale.ROOT)) {
        "text/css" -> "css"
        "image/png" -> "png"
        "image/jpeg" -> "jpg"
        "image/gif" -> "gif"
        "image/webp" -> "webp"
        "image/avif" -> "avif"
        "image/bmp" -> "bmp"
        "image/x-icon" -> "ico"
        "image/svg+xml" -> "svg"
        "image/heic" -> "heic"
        "image/heif" -> "heif"
        "font/woff" -> "woff"
        "font/woff2" -> "woff2"
        "font/ttf" -> "ttf"
        "font/otf" -> "otf"
        "video/mp4" -> "mp4"
        "video/webm" -> "webm"
        "video/ogg" -> "ogv"
        "audio/mpeg" -> "mp3"
        "audio/mp4" -> "m4a"
        "audio/ogg" -> "ogg"
        "audio/wav" -> "wav"
        else -> null
    }
}

internal class HtmlPreviewerMhtmlException(message: String) : IOException(message)

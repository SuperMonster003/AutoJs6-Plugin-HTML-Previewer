package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ContentResolver
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets
import java.util.Locale

internal class HtmlPreviewerTextReader(
    private val contentResolver: ContentResolver,
) {

    fun read(
        uri: Uri,
        maxBytes: Int,
        allowTruncated: Boolean = false,
    ): HtmlPreviewerTextReadResult {
        require(maxBytes > 0) { "maxBytes must be positive" }
        require(uri.scheme.equals(ContentResolver.SCHEME_CONTENT, ignoreCase = true)) {
            "Document URI must use the content scheme"
        }
        val byteResult = contentResolver.openInputStream(uri)?.use { input ->
            HtmlPreviewerTextCodec.readPrefix(input, maxBytes)
        } ?: throw IOException("Cannot open the previewer document")
        if (byteResult.isTruncated && !allowTruncated) {
            throw HtmlPreviewerTooLargeException(maxBytes)
        }
        return HtmlPreviewerTextReadResult(
            text = HtmlPreviewerTextCodec.decode(byteResult.bytes),
            isTruncated = byteResult.isTruncated,
        )
    }
}

internal data class HtmlPreviewerTextReadResult(
    val text: String,
    val isTruncated: Boolean,
)

internal data class HtmlPreviewerByteReadResult(
    val bytes: ByteArray,
    val isTruncated: Boolean,
)

internal object HtmlPreviewerTextCodec {

    fun readBounded(input: InputStream, maxBytes: Int): ByteArray {
        val result = readPrefix(input, maxBytes)
        if (result.isTruncated) throw HtmlPreviewerTooLargeException(maxBytes)
        return result.bytes
    }

    fun readPrefix(input: InputStream, maxBytes: Int): HtmlPreviewerByteReadResult {
        require(maxBytes > 0) { "maxBytes must be positive" }
        val output = ByteArrayOutputStream(minOf(DEFAULT_BUFFER_SIZE, maxBytes))
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var total = 0
        while (total < maxBytes) {
            val read = input.read(buffer, 0, minOf(buffer.size, maxBytes - total))
            if (read < 0) {
                return HtmlPreviewerByteReadResult(output.toByteArray(), isTruncated = false)
            }
            if (read == 0) {
                val oneByte = input.read()
                if (oneByte < 0) {
                    return HtmlPreviewerByteReadResult(output.toByteArray(), isTruncated = false)
                }
                total++
                output.write(oneByte)
                continue
            }
            total += read
            output.write(buffer, 0, read)
        }
        return HtmlPreviewerByteReadResult(
            bytes = output.toByteArray(),
            isTruncated = input.read() >= 0,
        )
    }

    fun decode(
        bytes: ByteArray,
        declaredCharset: String? = null,
    ): String {
        val bomEncoding = BOM_ENCODINGS.firstOrNull { bytes.startsWith(it.signature) }
        val charset = bomEncoding?.charset
            ?: declaredCharset?.let(::charsetForMimeLabel)
            ?: declaredHtmlCharset(bytes)
            ?: StandardCharsets.UTF_8
        val offset = bomEncoding?.signature?.size ?: 0
        val decoder = charset.newDecoder()
            .onMalformedInput(CodingErrorAction.REPLACE)
            .onUnmappableCharacter(CodingErrorAction.REPLACE)
        return decoder.decode(ByteBuffer.wrap(bytes, offset, bytes.size - offset)).toString()
    }

    private fun declaredHtmlCharset(bytes: ByteArray): Charset? {
        val scanLength = minOf(bytes.size, HTML_META_SCAN_BYTE_LIMIT)
        if (scanLength == 0) return null
        val prefix = String(bytes, 0, scanLength, StandardCharsets.ISO_8859_1)
        for (attributeSource in metaAttributeSources(prefix)) {
            val attributes = parseAttributes(attributeSource)
            attributes["charset"]
                ?.let(::charsetForHtmlLabel)
                ?.let { return it }

            if (attributes["http-equiv"]?.equals("content-type", ignoreCase = true) == true) {
                val label = attributes["content"]
                    ?.let(HTTP_EQUIV_CHARSET_PATTERN::find)
                    ?.groupValues
                    ?.get(1)
                label?.let(::charsetForHtmlLabel)?.let { return it }
            }
        }
        return null
    }

    private fun metaAttributeSources(prefix: String): Sequence<String> = sequence {
        var searchFrom = 0
        while (searchFrom < prefix.length) {
            val tagStart = prefix.indexOf('<', searchFrom)
            if (tagStart < 0) return@sequence
            if (prefix.startsWith("<!--", tagStart)) {
                val commentEnd = prefix.indexOf("-->", tagStart + 4)
                if (commentEnd < 0) return@sequence
                searchFrom = commentEnd + 3
                continue
            }

            val tagEnd = findTagEnd(prefix, tagStart + 1)
            if (tagEnd < 0) return@sequence
            var nameStart = tagStart + 1
            if (prefix.getOrNull(nameStart) == '/') nameStart++
            var nameEnd = nameStart
            while (prefix.getOrNull(nameEnd)?.isAsciiLetter() == true) nameEnd++
            if (
                prefix.regionMatches(
                    thisOffset = nameStart,
                    other = "meta",
                    otherOffset = 0,
                    length = 4,
                    ignoreCase = true,
                ) &&
                nameEnd - nameStart == 4 &&
                prefix.getOrNull(tagStart + 1) != '/' &&
                prefix.getOrNull(nameEnd).let { delimiter ->
                    delimiter == null || delimiter.isHtmlSpace() || delimiter == '/' || delimiter == '>'
                }
            ) {
                yield(prefix.substring(nameEnd, tagEnd))
            }
            searchFrom = tagEnd + 1
        }
    }

    private fun findTagEnd(text: String, start: Int): Int {
        var quote: Char? = null
        for (index in start until text.length) {
            val character = text[index]
            if (quote != null) {
                if (character == quote) quote = null
            } else {
                when (character) {
                    '\'', '"' -> quote = character
                    '>' -> return index
                }
            }
        }
        return -1
    }

    private fun parseAttributes(source: String): Map<String, String> {
        val attributes = linkedMapOf<String, String>()
        var position = 0
        while (position < source.length) {
            while (source.getOrNull(position)?.isHtmlSpace() == true || source.getOrNull(position) == '/') {
                position++
            }
            if (position >= source.length) break

            val nameStart = position
            while (
                position < source.length &&
                !source[position].isHtmlSpace() &&
                source[position] != '=' &&
                source[position] != '/'
            ) {
                position++
            }
            if (position == nameStart) {
                position++
                continue
            }
            val name = source.substring(nameStart, position).lowercase(Locale.ROOT)
            while (source.getOrNull(position)?.isHtmlSpace() == true) position++

            var value = ""
            if (source.getOrNull(position) == '=') {
                position++
                while (source.getOrNull(position)?.isHtmlSpace() == true) position++
                val quote = source.getOrNull(position).takeIf { it == '\'' || it == '"' }
                if (quote != null) {
                    position++
                    val valueStart = position
                    while (position < source.length && source[position] != quote) position++
                    value = source.substring(valueStart, position)
                    if (position < source.length) position++
                } else {
                    val valueStart = position
                    while (position < source.length && !source[position].isHtmlSpace()) position++
                    value = source.substring(valueStart, position).removeSuffix("/")
                }
            }
            if (name !in attributes) attributes[name] = value
        }
        return attributes
    }

    private fun charsetForHtmlLabel(rawLabel: String): Charset? {
        val label = rawLabel.trim().lowercase(Locale.ROOT)
        if (label.isEmpty() || label in REPLACEMENT_LABELS || label in PROHIBITED_HTML_LABELS) return null
        return when {
            label in UTF_16_LABELS -> StandardCharsets.UTF_8
            label == "x-user-defined" || label in WINDOWS_1252_LABELS -> charset("windows-1252")
            label in GBK_LABELS -> charset("GBK")
            label in BIG5_LABELS -> charset("Big5")
            label in SHIFT_JIS_LABELS -> charset("windows-31j") ?: charset("Shift_JIS")
            label in EUC_KR_LABELS -> charset("EUC-KR") ?: charset("x-windows-949")
            else -> charset(label)
        }
    }

    private fun charsetForMimeLabel(rawLabel: String): Charset? {
        val label = rawLabel.trim().lowercase(Locale.ROOT)
        if (label.isEmpty() || label in REPLACEMENT_LABELS || label in PROHIBITED_HTML_LABELS) return null
        return when {
            label in UTF_16_LABELS -> charset(label)
            label == "x-user-defined" || label in WINDOWS_1252_LABELS -> charset("windows-1252")
            label in GBK_LABELS -> charset("GBK")
            label in BIG5_LABELS -> charset("Big5")
            label in SHIFT_JIS_LABELS -> charset("windows-31j") ?: charset("Shift_JIS")
            label in EUC_KR_LABELS -> charset("EUC-KR") ?: charset("x-windows-949")
            else -> charset(label)
        }
    }

    private fun ByteArray.startsWith(prefix: ByteArray): Boolean =
        size >= prefix.size && prefix.indices.all { this[it] == prefix[it] }

    private fun Char.isAsciiLetter(): Boolean = this in 'A'..'Z' || this in 'a'..'z'

    private fun Char.isHtmlSpace(): Boolean = this == '\t' || this == '\n' || this == '\u000C' ||
        this == '\r' || this == ' '

    private const val DEFAULT_BUFFER_SIZE = 8 * 1024
    private const val HTML_META_SCAN_BYTE_LIMIT = 1024

    private val HTTP_EQUIV_CHARSET_PATTERN = Regex(
        pattern = """(?i)(?:^|[;\s])charset\s*=\s*[\"']?\s*([a-z0-9._:+-]+)""",
    )

    private val UTF_16_LABELS = setOf(
        "csunicode",
        "iso-10646-ucs-2",
        "ucs-2",
        "unicode",
        "unicodefeff",
        "unicodefffe",
        "utf-16",
        "utf-16be",
        "utf-16le",
    )

    private val WINDOWS_1252_LABELS = setOf(
        "ansi_x3.4-1968",
        "ascii",
        "cp1252",
        "cp819",
        "csisolatin1",
        "ibm819",
        "iso-8859-1",
        "iso-ir-100",
        "iso8859-1",
        "iso88591",
        "iso_8859-1",
        "iso_8859-1:1987",
        "l1",
        "latin1",
        "us-ascii",
        "windows-1252",
        "x-cp1252",
    )

    private val GBK_LABELS = setOf(
        "chinese",
        "csgb2312",
        "csiso58gb231280",
        "gb2312",
        "gb_2312",
        "gb_2312-80",
        "gbk",
        "iso-ir-58",
        "x-gbk",
    )

    private val BIG5_LABELS = setOf("big5", "big5-hkscs", "cn-big5", "csbig5", "x-x-big5")

    private val SHIFT_JIS_LABELS = setOf(
        "csshiftjis",
        "ms932",
        "ms_kanji",
        "shift-jis",
        "shift_jis",
        "sjis",
        "windows-31j",
        "x-sjis",
    )

    private val EUC_KR_LABELS = setOf(
        "cseuckr",
        "csksc56011987",
        "euc-kr",
        "iso-ir-149",
        "korean",
        "ks_c_5601-1987",
        "ks_c_5601-1989",
        "ksc5601",
        "ksc_5601",
        "windows-949",
    )

    private val REPLACEMENT_LABELS = setOf(
        "csiso2022kr",
        "hz-gb-2312",
        "iso-2022-cn",
        "iso-2022-cn-ext",
        "iso-2022-kr",
        "replacement",
    )

    private val PROHIBITED_HTML_LABELS = setOf(
        "bocu-1",
        "cesu-8",
        "scsu",
        "unicode-1-1-utf-7",
        "utf-7",
        "utf-32",
        "utf-32be",
        "utf-32le",
    )

    private val BOM_ENCODINGS = listOfNotNull(
        charset("UTF-32BE")?.let {
            BomEncoding(it, byteArrayOf(0x00, 0x00, 0xFE.toByte(), 0xFF.toByte()))
        },
        charset("UTF-32LE")?.let {
            BomEncoding(it, byteArrayOf(0xFF.toByte(), 0xFE.toByte(), 0x00, 0x00))
        },
        BomEncoding(
            StandardCharsets.UTF_8,
            byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte()),
        ),
        BomEncoding(StandardCharsets.UTF_16BE, byteArrayOf(0xFE.toByte(), 0xFF.toByte())),
        BomEncoding(StandardCharsets.UTF_16LE, byteArrayOf(0xFF.toByte(), 0xFE.toByte())),
    )

    private fun charset(name: String): Charset? = runCatching { Charset.forName(name) }.getOrNull()

    private data class BomEncoding(
        val charset: Charset,
        val signature: ByteArray,
    )
}

internal class HtmlPreviewerTooLargeException(
    val limitBytes: Int,
) : IOException("Document exceeds the $limitBytes-byte previewer limit")

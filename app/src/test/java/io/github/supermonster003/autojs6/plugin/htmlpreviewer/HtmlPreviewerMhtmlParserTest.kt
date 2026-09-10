package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.jsoup.Jsoup
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.nio.charset.StandardCharsets

class HtmlPreviewerMhtmlParserTest {

    @Test
    fun startSelectsRootAndLocationCidAndCssReferencesBecomeVirtualResources() {
        val result = HtmlPreviewerMhtmlParser.parse(
            archive(
                topParameters = "type=\"text/html\"; start=\"<root-part>\"",
                parts = listOf(
                    part(
                        headers = """
                            Content-Type: image/png
                            Content-Transfer-Encoding: base64
                            Content-ID: <hero-image>
                            Content-Location: https://example.test/assets/hero.png
                        """,
                        body = "AQIDBA==",
                    ),
                    part(
                        headers = """
                            Content-Type: text/css; charset=utf-8
                            Content-Transfer-Encoding: quoted-printable
                            Content-Location: https://example.test/assets/site.css
                        """,
                        body = "body { background-image: url(hero.png); }",
                    ),
                    part(
                        headers = """
                            Content-Type: text/html; charset=utf-8
                            Content-ID: <root-part>
                            Content-Location: https://example.test/pages/index.html
                        """,
                        body = """
                            <html><head><link id="site-css" rel="stylesheet" href="../assets/site.css"></head>
                            <body><img id="hero" src="cid:hero-image"><script id="evil">run()</script></body></html>
                        """,
                    ),
                ),
            ),
        )

        assertTrue(result.sourceText.contains("cid:hero-image"))
        assertEquals(2, result.embeddedResources.size)
        val previewer = Jsoup.parse(result.previewerText)
        val imagePath = previewer.selectFirst("#hero")?.attr("src").orEmpty()
        val cssPath = previewer.selectFirst("#site-css")?.attr("href").orEmpty()
        assertTrue(imagePath.startsWith("${HtmlPreviewerWebOrigin.DOCUMENT_PATH_PREFIX}__mhtml__/"))
        assertTrue(cssPath.startsWith("${HtmlPreviewerWebOrigin.DOCUMENT_PATH_PREFIX}__mhtml__/"))
        assertArrayEquals(
            byteArrayOf(1, 2, 3, 4),
            result.embeddedResources.getValue(imagePath.removePrefix(HtmlPreviewerWebOrigin.DOCUMENT_PATH_PREFIX)).bytes,
        )
        val rewrittenCss = result.embeddedResources
            .getValue(cssPath.removePrefix(HtmlPreviewerWebOrigin.DOCUMENT_PATH_PREFIX))
            .bytes
            .toString(StandardCharsets.UTF_8)
        assertTrue(rewrittenCss.contains(imagePath))

        val sanitized = Jsoup.parse(HtmlPreviewerRenderer().render(result.previewerText))
        assertTrue(sanitized.select("#evil").isEmpty())
        assertEquals(imagePath, sanitized.selectFirst("#hero")?.attr("src"))
        assertEquals(cssPath, sanitized.selectFirst("#site-css")?.attr("href"))
    }

    @Test
    fun foldedContentTypeAndMissingStartUseTheFirstPartAsRoot() {
        val bytes = """
            MIME-Version: 1.0
            Content-Type: multipart/related;
             type="text/html";
             boundary="folded-boundary"

            --folded-boundary
            Content-Type: text/html
            Content-Location: https://example.test/index.html

            <h1>First root</h1>
            --folded-boundary
            Content-Type: text/html

            <h1>Second root</h1>
            --folded-boundary--
        """.trimIndent().withCrlf().toByteArray(StandardCharsets.ISO_8859_1)

        val result = HtmlPreviewerMhtmlParser.parse(bytes)

        assertTrue(result.sourceText.contains("First root"))
        assertFalse(result.sourceText.contains("Second root"))
    }

    @Test
    fun identityTransferEncodingsPreserveResourceBytes() {
        val result = HtmlPreviewerMhtmlParser.parse(
            archive(
                parts = listOf(
                    htmlPart("<img src=\"cid:seven\"><img src=\"cid:eight\"><img src=\"cid:binary\">"),
                    binaryPart("seven", "7bit", "seven"),
                    binaryPart("eight", "8bit", "eight"),
                    binaryPart("binary", "binary", "binary"),
                ),
            ),
        )

        val document = Jsoup.parse(result.previewerText)
        val resources = document.select("img").associate { image ->
            val id = image.attr("src").removePrefix(HtmlPreviewerWebOrigin.DOCUMENT_PATH_PREFIX)
            image.attr("src") to result.embeddedResources.getValue(id).bytes.toString(StandardCharsets.US_ASCII)
        }
        assertEquals(setOf("seven", "eight", "binary"), resources.values.toSet())
    }

    @Test
    fun quotedPrintableRootUsesItsDeclaredCharsetAndSoftLineBreaks() {
        val result = HtmlPreviewerMhtmlParser.parse(
            archive(
                parts = listOf(
                    part(
                        headers = """
                            Content-Type: text/html; charset=utf-8
                            Content-Transfer-Encoding: quoted-printable
                        """,
                        body = "<p>=E4=BD=A0=E5=A5=BD soft=\r\n break</p>",
                    ),
                ),
            ),
        )

        assertTrue(result.sourceText.contains("你好 soft break"))
    }

    @Test
    fun nestedAlternativeSelectsTheLastSupportedHtmlRepresentation() {
        val bytes = """
            MIME-Version: 1.0
            Content-Type: multipart/related; boundary="outer"; start="<root-container>"

            --outer
            Content-Type: multipart/alternative; boundary="inner"
            Content-ID: <root-container>

            --inner
            Content-Type: text/plain

            Plain fallback
            --inner
            Content-Type: text/html

            <p>HTML fallback</p>
            --inner--
            --outer
            Content-Type: image/png
            Content-ID: <unused>

            bytes
            --outer--
        """.trimIndent().withCrlf().toByteArray(StandardCharsets.ISO_8859_1)

        val result = HtmlPreviewerMhtmlParser.parse(bytes)

        assertTrue(result.sourceText.contains("HTML fallback"))
        assertFalse(result.sourceText.contains("Plain fallback"))
    }

    @Test
    fun baseElementAndAbsoluteRelativeCidAndFragmentReferencesResolveInsideArchive() {
        val result = HtmlPreviewerMhtmlParser.parse(
            archive(
                parts = listOf(
                    part(
                        headers = """
                            Content-Type: text/html
                            Content-Location: https://example.test/pages/index.html
                        """,
                        body = """
                            <base href="../assets/">
                            <img id="relative" src="picture.svg#symbol">
                            <img id="absolute" src="https://example.test/assets/picture.svg">
                            <img id="cid" src="cid:picture%40archive">
                        """,
                    ),
                    part(
                        headers = """
                            Content-Type: image/svg+xml
                            Content-ID: <picture@archive>
                            Content-Location: https://example.test/assets/picture.svg
                        """,
                        body = "<svg xmlns=\"http://www.w3.org/2000/svg\"></svg>",
                    ),
                ),
            ),
        )

        val document = Jsoup.parse(result.previewerText)
        val relative = document.selectFirst("#relative")?.attr("src").orEmpty()
        assertTrue(relative.endsWith(".svg#symbol"))
        assertEquals(relative.substringBefore('#'), document.selectFirst("#absolute")?.attr("src"))
        assertEquals(relative.substringBefore('#'), document.selectFirst("#cid")?.attr("src"))
        assertTrue(document.select("base").isEmpty())
    }

    @Test
    fun duplicateLocationIsAmbiguousAndIsNotRewritten() {
        val result = HtmlPreviewerMhtmlParser.parse(
            archive(
                parts = listOf(
                    part(
                        headers = """
                            Content-Type: text/html
                            Content-Location: https://example.test/index.html
                        """,
                        body = "<img id=\"duplicate\" src=\"shared.png\">",
                    ),
                    imagePart("one", "https://example.test/shared.png", "one"),
                    imagePart("two", "https://example.test/shared.png", "two"),
                ),
            ),
        )

        assertEquals("shared.png", Jsoup.parse(result.previewerText).selectFirst("#duplicate")?.attr("src"))
        assertEquals(2, result.embeddedResources.size)
    }

    @Test
    fun unsupportedPartsAreNeverExposedAsVirtualResources() {
        val result = HtmlPreviewerMhtmlParser.parse(
            archive(
                parts = listOf(
                    htmlPart("<script src=\"cid:payload\"></script><iframe src=\"cid:nested\"></iframe>"),
                    part(
                        headers = "Content-Type: application/javascript\nContent-ID: <payload>",
                        body = "run()",
                    ),
                    part(
                        headers = "Content-Type: text/html\nContent-ID: <nested>",
                        body = "<p>Nested page</p>",
                    ),
                ),
            ),
        )

        assertTrue(result.embeddedResources.isEmpty())
        val sanitized = Jsoup.parse(HtmlPreviewerRenderer().render(result.previewerText))
        assertTrue(sanitized.select("script, iframe").isEmpty())
    }

    @Test
    fun malformedBoundaryStartAndTransferEncodingAreRejected() {
        assertMhtmlFailure(
            """
                MIME-Version: 1.0
                Content-Type: multipart/related; boundary="missing-close"

                --missing-close
                Content-Type: text/html

                <p>Incomplete</p>
            """,
        )
        assertMhtmlFailure(
            archiveText(
                topParameters = "start=\"<missing>\"",
                parts = listOf(htmlPart("<p>Root</p>")),
            ),
        )
        assertMhtmlFailure(
            archiveText(
                parts = listOf(
                    part(
                        headers = "Content-Type: text/html\nContent-Transfer-Encoding: gzip",
                        body = "payload",
                    ),
                ),
            ),
        )
    }

    @Test
    fun parserEnforcesPartDepthHeaderAndDecodedBodyLimits() {
        val twoParts = archive(parts = listOf(htmlPart("<p>Root</p>"), imagePart("x", "x.png", "x")))
        assertThrows(HtmlPreviewerMhtmlException::class.java) {
            HtmlPreviewerMhtmlParser.parse(
                twoParts,
                HtmlPreviewerMhtmlParser.Limits(maxPartCount = 2),
            )
        }

        val nested = """
            Content-Type: multipart/related; boundary="outer"

            --outer
            Content-Type: multipart/alternative; boundary="inner"

            --inner
            Content-Type: text/html

            <p>Nested</p>
            --inner--
            --outer--
        """.trimIndent().withCrlf().toByteArray(StandardCharsets.ISO_8859_1)
        assertThrows(HtmlPreviewerMhtmlException::class.java) {
            HtmlPreviewerMhtmlParser.parse(
                nested,
                HtmlPreviewerMhtmlParser.Limits(maxDepth = 1),
            )
        }

        val longHeader = archive(parts = listOf(htmlPart("<p>Root</p>", extraHeader = "X-Long: ${"x".repeat(100)}")))
        assertThrows(HtmlPreviewerMhtmlException::class.java) {
            HtmlPreviewerMhtmlParser.parse(
                longHeader,
                HtmlPreviewerMhtmlParser.Limits(
                    maxHeaderBytesPerEntity = 80,
                    maxTotalHeaderBytes = 160,
                ),
            )
        }

        val manyHeaders = archive(
            parts = listOf(
                htmlPart("<p>Root</p>", extraHeader = "X-First: ${"a".repeat(70)}"),
                imagePart("resource", "resource.png", "bytes"),
            ),
        )
        assertThrows(HtmlPreviewerMhtmlException::class.java) {
            HtmlPreviewerMhtmlParser.parse(
                manyHeaders,
                HtmlPreviewerMhtmlParser.Limits(
                    maxHeaderBytesPerEntity = 256,
                    maxTotalHeaderBytes = 256,
                ),
            )
        }

        val decoded = archive(parts = listOf(htmlPart("<p>${"x".repeat(80)}</p>")))
        assertThrows(HtmlPreviewerMhtmlException::class.java) {
            HtmlPreviewerMhtmlParser.parse(
                decoded,
                HtmlPreviewerMhtmlParser.Limits(maxDecodedBytes = 32),
            )
        }
    }

    @Test
    fun topLevelMustBeRelatedAndRootMustBeHtml() {
        assertMhtmlFailure("Content-Type: text/html\n\n<p>Not an archive</p>")
        assertMhtmlFailure(
            archiveText(
                parts = listOf(
                    part(headers = "Content-Type: text/plain", body = "No HTML"),
                ),
            ),
        )
    }

    private fun assertMhtmlFailure(source: String) {
        assertThrows(HtmlPreviewerMhtmlException::class.java) {
            HtmlPreviewerMhtmlParser.parse(
                source.trimIndent().withCrlf().toByteArray(StandardCharsets.ISO_8859_1),
            )
        }
    }

    private fun archive(
        topParameters: String = "type=\"text/html\"",
        parts: List<String>,
    ): ByteArray = archiveText(topParameters, parts)
        .withCrlf()
        .toByteArray(StandardCharsets.ISO_8859_1)

    private fun archiveText(
        topParameters: String = "type=\"text/html\"",
        parts: List<String>,
    ): String = buildString {
        appendLine("MIME-Version: 1.0")
        append("Content-Type: multipart/related; boundary=\"$BOUNDARY\"")
        if (topParameters.isNotBlank()) append("; $topParameters")
        appendLine()
        appendLine()
        parts.forEach { bodyPart ->
            appendLine("--$BOUNDARY")
            append(bodyPart.trimIndent().trimEnd('\n', '\r'))
            appendLine()
        }
        appendLine("--$BOUNDARY--")
    }

    private fun part(headers: String, body: String): String = buildString {
        append(headers.trimIndent().replace("\r\n", "\n"))
        appendLine()
        appendLine()
        append(body)
    }

    private fun htmlPart(
        body: String,
        extraHeader: String? = null,
    ): String = part(
        headers = buildString {
            append("Content-Type: text/html; charset=utf-8")
            extraHeader?.let { append('\n').append(it) }
        },
        body = body,
    )

    private fun binaryPart(
        id: String,
        encoding: String,
        body: String,
    ): String = part(
        headers = """
            Content-Type: image/png
            Content-ID: <$id>
            Content-Transfer-Encoding: $encoding
        """,
        body = body,
    )

    private fun imagePart(
        id: String,
        location: String,
        body: String,
    ): String = part(
        headers = """
            Content-Type: image/png
            Content-ID: <$id>
            Content-Location: $location
        """,
        body = body,
    )

    private fun String.withCrlf(): String = replace("\r\n", "\n").replace("\n", "\r\n")

    companion object {
        private const val BOUNDARY = "html-previewer-test-boundary"
    }
}

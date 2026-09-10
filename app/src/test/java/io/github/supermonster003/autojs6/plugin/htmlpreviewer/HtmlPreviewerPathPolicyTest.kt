package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlPreviewerPathPolicyTest {

    @Test
    fun relativeResourcesUseOnlyWhitelistedMimeTypes() {
        val resources = mapOf(
            "styles/site.css" to "text/css",
            "images/picture.PNG" to "image/png",
            "fonts/body.woff2" to "font/woff2",
            "media/movie.mp4" to "video/mp4",
            "media/sound.ogg" to "audio/ogg",
        )

        resources.forEach { (path, expectedMimeType) ->
            assertTrue(path, HtmlPreviewerPathPolicy.isSafeRelativePath(path))
            assertEquals(expectedMimeType, HtmlPreviewerPathPolicy.mimeType(path))
        }
    }

    @Test
    fun traversalEncodedAndBackslashPathsAreRejected() {
        val unsafePaths = listOf(
            "../outside.png",
            "images/../picture.png",
            "images/%2e%2e/outside.png",
            "images%2Fpicture.png",
            "images\\picture.png",
            "/images/picture.png",
            "images//picture.png",
            "images/./picture.png",
            "images/picture.png?size=large",
            "images/picture.png#fragment",
            "images/picture.png:alternate",
        )

        unsafePaths.forEach { path ->
            assertFalse("$path should fail validation", HtmlPreviewerPathPolicy.isSafeRelativePath(path))
        }
    }

    @Test
    fun unsupportedMimeTypesAreRejected() {
        listOf("payload.js", "document.html", "notes.txt", "archive.zip").forEach { path ->
            assertNull(path, HtmlPreviewerPathPolicy.mimeType(path))
        }
    }

    @Test
    fun mhtmlResourcesReuseTheExistingPathMimeAllowlist() {
        val mediaTypes = listOf(
            "text/css",
            "image/png",
            "image/jpeg",
            "image/svg+xml",
            "font/woff2",
            "video/mp4",
            "audio/ogg",
        )

        mediaTypes.forEach { mediaType ->
            val extension = requireNotNull(HtmlPreviewerMhtmlResourcePolicy.extension(mediaType))
            assertEquals(mediaType, HtmlPreviewerPathPolicy.mimeType("archive/resource.$extension"))
        }
        assertNull(HtmlPreviewerMhtmlResourcePolicy.extension("application/javascript"))
        assertNull(HtmlPreviewerMhtmlResourcePolicy.extension("text/html"))
    }

    @Test
    fun ordinaryNestedRelativePathIsAccepted() {
        assertTrue(HtmlPreviewerPathPolicy.isSafeRelativePath("images/diagrams/previewer.svg"))
    }

    @Test
    fun onlyBundledHtmlStylesheetCanLoad() {
        assertTrue(HtmlPreviewerAssetPolicy.isAllowed("html-base.css"))
        assertFalse(HtmlPreviewerAssetPolicy.isAllowed("../html-base.css"))
        assertFalse(HtmlPreviewerAssetPolicy.isAllowed("theme.css"))
        assertFalse(HtmlPreviewerAssetPolicy.isAllowed("custom.css"))
    }
}

@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.net.Uri
import androidx.test.runner.AndroidJUnit4
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HtmlPreviewerIntentPolicyInstrumentationTest {

    private val parentUri = Uri.parse("content://org.autojs.test.fileprovider/root/documents")
    private val documentUri = Uri.parse("content://org.autojs.test.fileprovider/root/documents/index.html")

    @Test
    fun completeUriOnlyExplorerContractIsAccepted() {
        val resolved = HtmlPreviewerIntentPolicy.resolve(validIntent())

        assertNotNull(resolved)
        assertEquals(documentUri, resolved?.documentUri)
        assertEquals(parentUri, resolved?.parentUri)
        assertEquals("index.html", resolved?.displayName)
        assertEquals(HtmlPreviewerDocumentFormat.HTML, resolved?.format)
    }

    @Test
    fun mhtmlExplorerContractSelectsTheArchivePipeline() {
        val resolved = HtmlPreviewerIntentPolicy.resolve(
            Intent(validIntent())
                .setDataAndType(documentUri, "multipart/related")
                .putExtra(ExplorerActionIntentExtras.DISPLAY_NAME, "saved-page.mhtml"),
        )

        assertNotNull(resolved)
        assertEquals(HtmlPreviewerDocumentFormat.MHTML, resolved?.format)
    }

    @Test
    fun anOversizedDeclarationRemainsEligibleForExplicitTruncatedPreviewer() {
        val resolved = HtmlPreviewerIntentPolicy.resolve(
            Intent(validIntent()).putExtra(
                ExplorerActionIntentExtras.SIZE,
                HtmlPreviewerActivity.MAX_HTML_BYTES.toLong() + 1L,
            ),
        )

        assertNotNull(resolved)
    }

    @Test
    fun actionProtocolAndGrantFlagsAreMandatory() {
        assertNull(HtmlPreviewerIntentPolicy.resolve(Intent(validIntent()).setAction(Intent.ACTION_VIEW)))
        assertNull(
            HtmlPreviewerIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, 3),
            ),
        )
        assertNull(
            HtmlPreviewerIntentPolicy.resolve(
                Intent(validIntent()).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                },
            ),
        )
    }

    @Test
    fun legacyEnvelopeAcceptsMinimumAuditedAndFutureHostBuilds() {
        listOf(5269L, 5279L, 6000L).forEach { hostVersionCode ->
            val resolved = HtmlPreviewerIntentPolicy.resolve(
                Intent(validIntent()).putExtra(
                    ExplorerActionIntentExtras.HOST_VERSION_CODE,
                    hostVersionCode,
                ),
            )

            assertNotNull("Expected host build $hostVersionCode to be accepted", resolved)
        }
    }

    @Test
    fun oldMissingOrNewProtocolHostEnvelopeIsRejected() {
        assertNull(
            HtmlPreviewerIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.HOST_VERSION_CODE, 5268L),
            ),
        )
        assertNull(
            HtmlPreviewerIntentPolicy.resolve(
                Intent(validIntent()).apply {
                    removeExtra(ExplorerActionIntentExtras.HOST_VERSION_CODE)
                },
            ),
        )
        assertNull(
            HtmlPreviewerIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, 22),
            ),
        )
    }

    @Test
    fun legacyEnvelopeRejectsAnAdditionalClipTarget() {
        val intent = validIntent().apply {
            clipData?.addItem(
                ClipData.Item(
                    Uri.parse("content://org.autojs.test.fileprovider/root/documents/second.html"),
                ),
            )
        }

        assertNull(HtmlPreviewerIntentPolicy.resolve(intent))
    }

    @Test
    fun targetParentAndClipDataMustDescribeTheSameContentTree() {
        assertNull(
            HtmlPreviewerIntentPolicy.resolve(
                Intent(validIntent()).setData(Uri.parse("file:///sdcard/index.html")),
            ),
        )
        assertNull(
            HtmlPreviewerIntentPolicy.resolve(
                Intent(validIntent()).putExtra(
                    ExplorerActionIntentExtras.PARENT_URI,
                    Uri.parse("content://org.autojs.test.fileprovider/root/other"),
                ),
            ),
        )
        assertNull(
            HtmlPreviewerIntentPolicy.resolve(
                Intent(validIntent()).apply {
                    clipData = ClipData(
                        ClipDescription("Previewer target", arrayOf("text/html")),
                        ClipData.Item(documentUri),
                    )
                },
            ),
        )
    }

    private fun validIntent(): Intent {
        val clipData = ClipData(
            ClipDescription("Previewer target", arrayOf("text/html")),
            ClipData.Item(documentUri),
        ).apply {
            addItem(ClipData.Item(parentUri))
        }
        return Intent(ExplorerActionPluginActions.EXECUTE)
            .setDataAndType(documentUri, "text/html")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            .putExtra(ExplorerActionIntentExtras.ACTION_ID, HtmlPreviewerPlugin.ID)
            .putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, HtmlPreviewerPlugin.PROTOCOL_VERSION)
            .putExtra(
                ExplorerActionIntentExtras.HOST_VERSION_CODE,
                HtmlPreviewerExplorerCompatibility.maximumAuditedHostVersionCode,
            )
            .putExtra(ExplorerActionIntentExtras.DISPLAY_NAME, "index.html")
            .putExtra(ExplorerActionIntentExtras.SIZE, 1024L)
            .putExtra(ExplorerActionIntentExtras.PARENT_URI, parentUri)
            .putExtra(
                ExplorerActionIntentExtras.SOURCE_SURFACE,
                ExplorerActionIntentValues.SOURCE_SURFACE_MAIN,
            )
            .apply { this.clipData = clipData }
    }
}

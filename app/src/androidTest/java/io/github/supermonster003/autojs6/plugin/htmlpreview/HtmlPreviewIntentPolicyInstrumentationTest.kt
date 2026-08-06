@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreview

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.net.Uri
import androidx.test.runner.AndroidJUnit4
import org.autojs.plugin.explorer.api.ExplorerActionIntentExtras
import org.autojs.plugin.explorer.api.ExplorerActionIntentValues
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HtmlPreviewIntentPolicyInstrumentationTest {

    private val parentUri = Uri.parse("content://org.autojs.test.fileprovider/root/documents")
    private val documentUri = Uri.parse("content://org.autojs.test.fileprovider/root/documents/index.html")

    @Test
    fun completeUriOnlyExplorerContractIsAccepted() {
        val resolved = HtmlPreviewIntentPolicy.resolve(validIntent())

        assertNotNull(resolved)
        assertEquals(documentUri, resolved?.documentUri)
        assertEquals(parentUri, resolved?.parentUri)
        assertEquals("index.html", resolved?.displayName)
    }

    @Test
    fun actionProtocolAndGrantFlagsAreMandatory() {
        assertNull(HtmlPreviewIntentPolicy.resolve(Intent(validIntent()).setAction(Intent.ACTION_VIEW)))
        assertNull(
            HtmlPreviewIntentPolicy.resolve(
                Intent(validIntent()).putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, 2),
            ),
        )
        assertNull(
            HtmlPreviewIntentPolicy.resolve(
                Intent(validIntent()).apply {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                },
            ),
        )
    }

    @Test
    fun targetParentAndClipDataMustDescribeTheSameContentTree() {
        assertNull(
            HtmlPreviewIntentPolicy.resolve(
                Intent(validIntent()).setData(Uri.parse("file:///sdcard/index.html")),
            ),
        )
        assertNull(
            HtmlPreviewIntentPolicy.resolve(
                Intent(validIntent()).putExtra(
                    ExplorerActionIntentExtras.PARENT_URI,
                    Uri.parse("content://org.autojs.test.fileprovider/root/other"),
                ),
            ),
        )
        assertNull(
            HtmlPreviewIntentPolicy.resolve(
                Intent(validIntent()).apply {
                    clipData = ClipData(
                        ClipDescription("Preview target", arrayOf("text/html")),
                        ClipData.Item(documentUri),
                    )
                },
            ),
        )
    }

    private fun validIntent(): Intent {
        val clipData = ClipData(
            ClipDescription("Preview target", arrayOf("text/html")),
            ClipData.Item(documentUri),
        ).apply {
            addItem(ClipData.Item(parentUri))
        }
        return Intent(ExplorerActionPluginActions.EXECUTE)
            .setDataAndType(documentUri, "text/html")
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            .putExtra(ExplorerActionIntentExtras.ACTION_ID, HtmlPreviewPlugin.ID)
            .putExtra(ExplorerActionIntentExtras.PROTOCOL_VERSION, ExplorerActionProtocol.VERSION)
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

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HtmlPreviewerExplorerCompatibilityTest {

    @Test
    fun auditedContractPinsTheBundledV1ApiAndCurrentHostCheckpoint() {
        assertEquals(1, ExplorerActionProtocol.VERSION)
        assertEquals(2, HtmlPreviewerExplorerCompatibility.declaredProtocolVersion)
        assertEquals(5269L, HtmlPreviewerExplorerCompatibility.minimumHostVersionCode)
        assertEquals(5279L, HtmlPreviewerExplorerCompatibility.maximumAuditedHostVersionCode)
        assertEquals(22, HtmlPreviewerExplorerCompatibility.maximumAuditedHostProtocolVersion)
        assertEquals(
            HtmlPreviewerExplorerCompatibility.minimumHostVersionCode,
            HtmlPreviewerPlugin.REQUIRED_HOST_VERSION,
        )
        assertEquals(
            HtmlPreviewerExplorerCompatibility.declaredProtocolVersion,
            HtmlPreviewerPlugin.PROTOCOL_VERSION,
        )
    }

    @Test
    fun hostProtocolCheckpointsAreStrictlyIncreasingAndEndAtTheAuditBoundary() {
        val checkpoints = HtmlPreviewerExplorerCompatibility.hostProtocolCheckpoints

        assertEquals(
            listOf(5268L to 1, 5269L to 3, 5276L to 21, 5277L to 22, 5279L to 22),
            checkpoints.map { it.hostVersionCode to it.maximumProtocolVersion },
        )
        assertTrue(checkpoints.zipWithNext().all { (left, right) ->
            left.hostVersionCode < right.hostVersionCode &&
                left.maximumProtocolVersion <= right.maximumProtocolVersion
        })
        assertEquals(
            HtmlPreviewerExplorerCompatibility.maximumAuditedHostVersionCode,
            checkpoints.last().hostVersionCode,
        )
        assertEquals(
            HtmlPreviewerExplorerCompatibility.maximumAuditedHostProtocolVersion,
            checkpoints.last().maximumProtocolVersion,
        )
    }

    @Test
    fun hostVersionClassificationRejectsOldAndSeparatesAuditedFromFuture() {
        assertFalse(HtmlPreviewerExplorerCompatibility.acceptsHostVersionCode(5268L))
        assertTrue(HtmlPreviewerExplorerCompatibility.acceptsHostVersionCode(5269L))
        assertTrue(HtmlPreviewerExplorerCompatibility.acceptsHostVersionCode(6000L))
        assertEquals(
            HtmlPreviewerExplorerCompatibility.HostAuditStatus.UNSUPPORTED,
            HtmlPreviewerExplorerCompatibility.auditStatus(5268L),
        )
        assertEquals(
            HtmlPreviewerExplorerCompatibility.HostAuditStatus.AUDITED,
            HtmlPreviewerExplorerCompatibility.auditStatus(5277L),
        )
        assertEquals(
            HtmlPreviewerExplorerCompatibility.HostAuditStatus.FORWARD_COMPATIBLE_UNAUDITED,
            HtmlPreviewerExplorerCompatibility.auditStatus(5280L),
        )
    }
}

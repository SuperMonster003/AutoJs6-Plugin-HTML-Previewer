@file:Suppress("DEPRECATION")

package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import org.autojs.plugin.common.api.PluginCapabilityKeys
import org.autojs.plugin.explorer.api.ExplorerActionCapabilityKeys
import org.autojs.plugin.explorer.api.ExplorerActionCatalogKeys
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions
import org.autojs.plugin.explorer.api.ExplorerActionPluginIds
import org.autojs.plugin.explorer.api.ExplorerActionPluginPermissions
import org.autojs.plugin.explorer.api.ExplorerActionProtocol
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PluginContractInstrumentationTest {

    @Test
    fun serviceReturnsBinderForExplicitActionlessBinding() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = Intent().setComponent(ComponentName(context, ExplorerActionService::class.java))

        assertNotNull(ExplorerActionService().onBind(intent))
    }

    @Test
    fun pluginInfoDeclaresAbiIndependentExplorerEngine() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val info = context.htmlPreviewerPluginInfo()

        assertEquals(HtmlPreviewerPlugin.ID, info.id)
        assertEquals(ExplorerActionPluginIds.ENGINE, info.engine)
        assertArrayEquals(emptyArray<String>(), info.supportedAbis)
        assertTrue(info.instruction?.isNotBlank() == true)
        assertEquals(
            HtmlPreviewerPlugin.REQUIRED_HOST_VERSION,
            info.capabilities?.getLong(PluginCapabilityKeys.REQUIRES_HOST_VERSION),
        )
        assertEquals(
            HtmlPreviewerPlugin.PROTOCOL_VERSION,
            info.capabilities?.getInt(ExplorerActionCapabilityKeys.PROTOCOL_VERSION),
        )
        assertEquals(2, HtmlPreviewerPlugin.PROTOCOL_VERSION)
    }

    @Test
    fun catalogUsesParcelableBundleAndStringArrayLists() {
        val catalog = htmlPreviewerActionCatalog()
        val actions = catalog.getParcelableArrayList<Bundle>(ExplorerActionCatalogKeys.ACTIONS)
        val action = actions?.single { it.getString(ExplorerActionCatalogKeys.ID) == HtmlPreviewerPlugin.ID }
        assertEquals(2, actions?.size)
        val primary = actions?.single { it.getString(ExplorerActionCatalogKeys.ID) == HtmlPreviewerPlugin.PRIMARY_ACTION_ID }
        assertEquals(2, primary?.getInt(ExplorerActionCatalogKeys.PLACEMENT))
        assertEquals(HtmlPreviewerPlugin.ACTIVITY_CLASS_NAME, primary?.getString(ExplorerActionCatalogKeys.ACTIVITY_CLASS_NAME))

        assertEquals(
            HtmlPreviewerPlugin.PROTOCOL_VERSION,
            catalog.getInt(ExplorerActionCatalogKeys.PROTOCOL_VERSION),
        )
        assertNotNull(action)
        assertEquals(HtmlPreviewerPlugin.ID, action?.getString(ExplorerActionCatalogKeys.ID))
        assertEquals(
            "action_html_previewer",
            action?.getString(ExplorerActionCatalogKeys.LABEL_RESOURCE_NAME),
        )
        assertEquals(
            HtmlPreviewerPlugin.ACTIVITY_CLASS_NAME,
            action?.getString(ExplorerActionCatalogKeys.ACTIVITY_CLASS_NAME),
        )
        assertEquals(
            listOf(
                "text/html",
                "application/xhtml+xml",
            ),
            action?.getStringArrayList(ExplorerActionCatalogKeys.MIME_TYPES),
        )
        assertEquals(
            HtmlPreviewerPlugin.EXTENSIONS.toList(),
            action?.getStringArrayList(ExplorerActionCatalogKeys.EXTENSIONS),
        )
        assertEquals(
            setOf(
                ExplorerActionCatalogKeys.ID,
                ExplorerActionCatalogKeys.LABEL_RESOURCE_NAME,
                ExplorerActionCatalogKeys.LABEL_FALLBACK,
                ExplorerActionCatalogKeys.ACTIVITY_CLASS_NAME,
                ExplorerActionCatalogKeys.PRIORITY,
                ExplorerActionCatalogKeys.TARGET_KIND,
                ExplorerActionCatalogKeys.ACCESS_MODE,
                ExplorerActionCatalogKeys.PLACEMENT,
                ExplorerActionCatalogKeys.MIME_TYPES,
                ExplorerActionCatalogKeys.EXTENSIONS,
            ),
            action?.keySet(),
        )

        val labelResourceName = action?.getString(ExplorerActionCatalogKeys.LABEL_RESOURCE_NAME).orEmpty()
        assertTrue(contextResources().getIdentifier(labelResourceName, "string", targetPackageName()) != 0)
    }

    @Test
    fun manifestProtectsAndExportsTheDiscoveryAndExecutionComponents() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val packageManager = context.packageManager
        val serviceInfo = packageManager.getServiceInfo(
            ComponentName(context, ExplorerActionService::class.java),
            0,
        )
        val activityInfo = packageManager.getActivityInfo(
            ComponentName(context, HtmlPreviewerActivity::class.java),
            0,
        )

        assertTrue(serviceInfo.exported)
        assertEquals(ExplorerActionPluginPermissions.PLUGIN, serviceInfo.permission)
        assertTrue(activityInfo.exported)
        assertEquals(ExplorerActionPluginPermissions.PLUGIN, activityInfo.permission)

        val discovery = packageManager.queryIntentServices(
            Intent(ExplorerActionPluginActions.EXPLORER_ACTION).setPackage(context.packageName),
            0,
        )
        assertTrue(discovery.any { it.serviceInfo.name == ExplorerActionService::class.java.name })

        val execution = packageManager.queryIntentActivities(
            Intent(ExplorerActionPluginActions.EXECUTE)
                .addCategory(Intent.CATEGORY_DEFAULT)
                .setPackage(context.packageName),
            0,
        )
        assertTrue(execution.any { it.activityInfo.name == HtmlPreviewerActivity::class.java.name })
    }

    private fun contextResources() =
        InstrumentationRegistry.getInstrumentation().targetContext.resources

    private fun targetPackageName() =
        InstrumentationRegistry.getInstrumentation().targetContext.packageName
}

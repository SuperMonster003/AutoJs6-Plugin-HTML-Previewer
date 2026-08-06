package io.github.supermonster003.autojs6.plugin.htmlpreview

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.autojs.plugin.explorer.api.IExplorerActionPlugin
import org.autojs.plugin.explorer.api.ExplorerActionPluginActions

class ExplorerActionService : Service() {

    private val binder = object : IExplorerActionPlugin.Stub() {
        override fun getInfo() = htmlPreviewPluginInfo()

        override fun getActionCatalog() = htmlPreviewActionCatalog()
    }

    override fun onBind(intent: Intent?): IBinder? =
        binder.takeIf { intent?.action == ExplorerActionPluginActions.EXPLORER_ACTION }
}

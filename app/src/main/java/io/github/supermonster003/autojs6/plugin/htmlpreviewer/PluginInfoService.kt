package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.app.Service
import android.content.Intent
import android.os.IBinder
import org.autojs.plugin.common.api.IPluginInfoProvider

class PluginInfoService : Service() {
    private val binder = object : IPluginInfoProvider.Stub() {
        override fun getInfo() = htmlPreviewerPluginInfo()
    }

    override fun onBind(intent: Intent?): IBinder = binder
}

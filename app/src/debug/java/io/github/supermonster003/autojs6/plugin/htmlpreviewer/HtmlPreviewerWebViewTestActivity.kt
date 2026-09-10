package io.github.supermonster003.autojs6.plugin.htmlpreviewer

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView

class HtmlPreviewerWebViewTestActivity : Activity() {

    lateinit var webView: WebView
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webView = WebView(this)
        setContentView(
            webView,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
    }
}

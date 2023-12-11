package app.priceguard.ui.additem.link

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.databinding.ActivityLinkHelperWebViewBinding

class LinkHelperWebViewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLinkHelperWebViewBinding
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLinkHelperWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        webView = WebView(this)
        webView.loadUrl("https://info-kr.priceguard.app/")
        binding.wbLinkHelper.addView(webView)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.wbLinkHelper.removeAllViews()
        webView.clearHistory()
        webView.clearCache(true)
        webView.loadUrl("about:blank")
        webView.pauseTimers()
        webView.destroy()
    }
}

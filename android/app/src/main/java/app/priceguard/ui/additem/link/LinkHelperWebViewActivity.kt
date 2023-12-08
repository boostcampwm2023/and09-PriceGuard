package app.priceguard.ui.additem.link

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.databinding.ActivityLinkHelperWebViewBinding

class LinkHelperWebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLinkHelperWebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLinkHelperWebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.wbLinkHelper.loadUrl("https://info-kr.priceguard.app/")
    }
}

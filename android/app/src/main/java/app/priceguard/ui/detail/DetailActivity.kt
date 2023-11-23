package app.priceguard.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.R
import app.priceguard.databinding.ActivityDetailBinding
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val productDetailViewModel: ProductDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.viewModel = productDetailViewModel
        setContentView(binding.root)

        checkProductCode()
        observeEvent()
    }

    private fun checkProductCode() {
        val productCode = intent.getStringExtra("productCode")
        if (productCode == null) {
            // Invalid access
            showDialogAndExit(getString(R.string.error), getString(R.string.invalid_access))
        } else {
            productDetailViewModel.productCode = productCode
            productDetailViewModel.getDetails()
        }
    }

    private fun observeEvent() {
        repeatOnStarted {
            productDetailViewModel.event.collect { event ->
                when (event) {
                    ProductDetailViewModel.ProductDetailEvent.Logout -> {
                        showDialogAndExit(getString(R.string.error), getString(R.string.logged_out))
                    }

                    ProductDetailViewModel.ProductDetailEvent.NotFound -> {
                        showDialogAndExit(
                            getString(R.string.error),
                            getString(R.string.product_not_found)
                        )
                    }

                    ProductDetailViewModel.ProductDetailEvent.UnknownError -> {
                        showDialogAndExit(
                            getString(R.string.error),
                            getString(R.string.undefined_error)
                        )
                    }

                    is ProductDetailViewModel.ProductDetailEvent.OpenShoppingMall -> {
                        val redirectUrl =
                            "https://11stapp.11st.co.kr/?domain=m.11st.co.kr&appLnkWyCd=02&goUrl=${event.url}"
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
                        startActivity(browserIntent)
                    }
                }
            }
        }
    }

    private fun showDialogAndExit(title: String, message: String) {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm)) { _, _ -> finish() }
            .setCancelable(false)
            .create()
            .show()
    }
}

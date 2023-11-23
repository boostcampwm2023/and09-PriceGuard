package app.priceguard.ui.detail

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

        val productCode = intent.getStringExtra("productCode")
        if (productCode == null) {
            // Invalid access
            showDialogAndExit(getString(R.string.error), getString(R.string.invalid_access))
        } else {
            productDetailViewModel.productCode = productCode
        }

        binding.viewModel = productDetailViewModel
        setContentView(binding.root)

        repeatOnStarted {
            productDetailViewModel.event.collect { event ->
                when (event) {
                    ProductDetailViewModel.ProductDetailEvent.Logout -> {
                        showDialogAndExit(getString(R.string.error), getString(R.string.logged_out))
                    }
                    ProductDetailViewModel.ProductDetailEvent.NotFound -> {
                        showDialogAndExit(getString(R.string.error), getString(R.string.product_not_found))
                    }
                    ProductDetailViewModel.ProductDetailEvent.UnknownError -> {
                        showDialogAndExit(getString(R.string.error), getString(R.string.undefined_error))
                    }
                }
            }
        }
    }

    private fun checkProductCode(productCode: String?) {

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

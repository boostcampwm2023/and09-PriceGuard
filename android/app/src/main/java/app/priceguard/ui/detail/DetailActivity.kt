package app.priceguard.ui.detail

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.R
import app.priceguard.data.dto.ProductDeleteState
import app.priceguard.databinding.ActivityDetailBinding
import app.priceguard.ui.additem.AddItemActivity
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

        initListener()
        setNavigationButton()
        checkProductCode()
        observeEvent()
    }

    private fun initListener() {
        binding.btnDetailTrack.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra("productCode", productDetailViewModel.productCode)
            intent.putExtra("productTitle", productDetailViewModel.state.value.productName)
            intent.putExtra("productPrice", productDetailViewModel.state.value.price)
            intent.putExtra("isAdding", true)
            this@DetailActivity.startActivity(intent)
        }
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

                    ProductDetailViewModel.ProductDetailEvent.DeleteTracking -> {
                        showConfirmationDialog(
                            getString(R.string.stop_tracking_confirm),
                            getString(R.string.stop_tracking_detail)
                        ) { _, _ -> productDetailViewModel.deleteProductTracking() }
                    }

                    is ProductDetailViewModel.ProductDetailEvent.DeleteFailed -> {
                        when (event.errorType) {
                            ProductDeleteState.NOT_FOUND -> {
                                showToast(getString(R.string.product_not_found))
                            }

                            ProductDeleteState.INVALID_REQUEST -> {
                                showToast(getString(R.string.invalid_request))
                            }

                            ProductDeleteState.UNAUTHORIZED -> {
                                showToast(getString(R.string.logged_out))
                                finish()
                            }

                            ProductDeleteState.UNDEFINED_ERROR -> {
                                showToast(getString(R.string.undefined_error))
                            }

                            else -> {}
                        }
                    }

                    ProductDetailViewModel.ProductDetailEvent.DeleteSuccess -> {
                        showToast(getString(R.string.delete_success))
                        finish()
                    }
                }
            }
        }
    }

    private fun setNavigationButton() {
        binding.mtDetailTopbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun showConfirmationDialog(
        title: String,
        message: String,
        onConfirm: DialogInterface.OnClickListener
    ) {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm), onConfirm)
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
            .create()
            .show()
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

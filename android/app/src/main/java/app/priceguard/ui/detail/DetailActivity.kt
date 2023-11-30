package app.priceguard.ui.detail

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.R
import app.priceguard.data.dto.ProductErrorState
import app.priceguard.data.graph.ProductChartGridLine
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.ActivityDetailBinding
import app.priceguard.materialchart.data.GraphMode
import app.priceguard.ui.additem.AddItemActivity
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.ui.showConfirmationDialog
import app.priceguard.ui.util.ui.showPermissionDeniedDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenRepository: TokenRepository
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

    override fun onStart() {
        super.onStart()
        if (productDetailViewModel.state.value.isReady) {
            productDetailViewModel.getDetails(true)
        }
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

        binding.btnDetailEditPrice.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra("productCode", productDetailViewModel.productCode)
            intent.putExtra("productTitle", productDetailViewModel.state.value.productName)
            intent.putExtra("productPrice", productDetailViewModel.state.value.price)
            intent.putExtra("isAdding", false)
            this@DetailActivity.startActivity(intent)
        }
        binding.mbtgGraphPeriod.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.btn_period_day -> {
                        productDetailViewModel.changePeriod(GraphMode.DAY)
                    }

                    R.id.btn_period_week -> {
                        productDetailViewModel.changePeriod(GraphMode.WEEK)
                    }

                    R.id.btn_period_month -> {
                        productDetailViewModel.changePeriod(GraphMode.MONTH)
                    }

                    R.id.btn_period_quarter -> {
                        productDetailViewModel.changePeriod(GraphMode.QUARTER)
                    }

                    else -> {}
                }
            }
        }
    }

    private fun checkProductCode() {
        val productCode = intent.getStringExtra("productCode")
        if (productCode == null) {
            // Invalid access
            showDialogAndExit(getString(R.string.error), getString(R.string.invalid_access))
        } else {
            productDetailViewModel.productCode = productCode
            productDetailViewModel.getDetails(false)
        }
    }

    private fun observeEvent() {
        repeatOnStarted {
            productDetailViewModel.state.collect { state ->
                binding.chGraphDetail.dataset = state.chartData?.copy(
                    gridLines = listOf(
                        ProductChartGridLine(
                            resources.getString(R.string.target_price),
                            state.targetPrice?.toFloat() ?: 0F
                        ),
                        ProductChartGridLine(
                            resources.getString(R.string.lowest_price),
                            state.lowestPrice?.toFloat() ?: 0F
                        )
                    )
                )
            }
        }
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
                            ProductErrorState.NOT_FOUND -> {
                                showConfirmationDialog(
                                    getString(R.string.delete_product_failed),
                                    getString(R.string.product_not_found)
                                )
                            }

                            ProductErrorState.INVALID_REQUEST -> {
                                showConfirmationDialog(
                                    getString(R.string.delete_product_failed),
                                    getString(R.string.invalid_request)
                                )
                            }

                            ProductErrorState.PERMISSION_DENIED -> {
                                showPermissionDeniedDialog(tokenRepository)
                            }

                            else -> {
                                showConfirmationDialog(
                                    getString(R.string.delete_product_failed),
                                    getString(R.string.undefined_error)
                                )
                            }
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

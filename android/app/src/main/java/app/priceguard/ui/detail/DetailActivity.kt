package app.priceguard.ui.detail

import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.R
import app.priceguard.data.graph.ProductChartDataset
import app.priceguard.data.graph.ProductChartGridLine
import app.priceguard.data.repository.ProductErrorState
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.ActivityDetailBinding
import app.priceguard.materialchart.data.GraphMode
import app.priceguard.ui.additem.AddItemActivity
import app.priceguard.ui.home.HomeActivity
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

        setBackPressedCallback()
        initListener()
        setNavigationButton()
        checkProductCode(intent)
        observeEvent()
    }

    private fun setBackPressedCallback() {
        onBackPressedDispatcher.addCallback(this) {
            goToHomeActivityIfDeepLinked()
        }
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
            if (!isChecked) return@addOnButtonCheckedListener

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
            }
        }

        binding.btnDetailShare.setOnClickListener {
            val shareLink =
                getString(R.string.share_link_template, productDetailViewModel.productCode)

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, getString(R.string.share_product))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message_template, shareLink))
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            startActivity(shareIntent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            checkProductCode(intent)
        }
    }

    private fun checkProductCode(intent: Intent) {
        val productCode = intent.getStringExtra("productCode")
        val deepLink = intent.data
        val productCodeFromDeepLink = deepLink?.getQueryParameter("code")

        if (productCode == null && productCodeFromDeepLink == null) {
            showDialogAndExit(getString(R.string.error), getString(R.string.invalid_access))
            return
        }

        productCode?.let { code ->
            productDetailViewModel.productCode = code
            productDetailViewModel.getDetails(false)
            return
        }

        productCodeFromDeepLink?.let { code ->
            productDetailViewModel.productCode = code
            productDetailViewModel.getDetails(false)
        }
    }

    private fun observeEvent() {
        repeatOnStarted {
            productDetailViewModel.state.collect { state ->
                state.targetPrice ?: return@collect
                binding.chGraphDetail.dataset = ProductChartDataset(
                    showXAxis = true,
                    showYAxis = true,
                    isInteractive = true,
                    graphMode = state.graphMode,
                    xLabel = getString(R.string.date_text),
                    yLabel = getString(R.string.price_text),
                    data = state.chartData,
                    gridLines = getGridLines(state.targetPrice.toFloat())
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

    private fun getGridLines(targetPrice: Float): List<ProductChartGridLine> {
        return if (targetPrice < 0) {
            listOf()
        } else {
            listOf(
                ProductChartGridLine(
                    resources.getString(R.string.target_price),
                    targetPrice
                )
            )
        }
    }

    private fun setNavigationButton() {
        binding.mtDetailTopbar.setNavigationOnClickListener {
            goToHomeActivityIfDeepLinked()
        }
    }

    private fun goToHomeActivityIfDeepLinked() {
        if (intent.data != null && intent.data?.getQueryParameter("code") != null) {
            val intent = Intent(this@DetailActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } else {
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

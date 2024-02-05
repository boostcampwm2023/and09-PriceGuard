package app.priceguard.ui.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import app.priceguard.R
import app.priceguard.data.graph.ProductChartDataset
import app.priceguard.data.graph.ProductChartGridLine
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.databinding.ActivityDetailBinding
import app.priceguard.materialchart.data.GraphMode
import app.priceguard.ui.additem.AddItemActivity
import app.priceguard.ui.data.DialogConfirmAction
import app.priceguard.ui.home.HomeActivity
import app.priceguard.ui.util.ConfirmDialogFragment
import app.priceguard.ui.util.SystemNavigationColorState
import app.priceguard.ui.util.applySystemNavigationBarColor
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.showConfirmDialog
import app.priceguard.ui.util.showDialogWithLogout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DetailActivity : AppCompatActivity(), ConfirmDialogFragment.OnDialogResultListener {

    @Inject
    lateinit var tokenRepository: TokenRepository
    private lateinit var binding: ActivityDetailBinding
    private val productDetailViewModel: ProductDetailViewModel by viewModels()

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            binding.btnDetailShare.isEnabled = true
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.applySystemNavigationBarColor(SystemNavigationColorState.SURFACE)
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
            intent.putExtra("productShop", productDetailViewModel.state.value.shop)
            intent.putExtra("productCode", productDetailViewModel.productCode)
            intent.putExtra("productTitle", productDetailViewModel.state.value.productName)
            intent.putExtra("productPrice", productDetailViewModel.state.value.price)
            intent.putExtra("isAdding", true)
            this@DetailActivity.startActivity(intent)
        }

        binding.btnDetailEditPrice.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra("productShop", productDetailViewModel.state.value.shop)
            intent.putExtra("productCode", productDetailViewModel.productCode)
            intent.putExtra("productTitle", productDetailViewModel.state.value.productName)
            intent.putExtra("productPrice", productDetailViewModel.state.value.price)
            intent.putExtra("productTargetPrice", productDetailViewModel.state.value.targetPrice)
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
            binding.btnDetailShare.isEnabled = false
            val shareLink = if (productDetailViewModel.productShop == "11번가") {
                getString(R.string.share_link_template, "11st", productDetailViewModel.productCode)
            } else {
                getString(R.string.share_link_template, "naver", productDetailViewModel.productCode)
            }

            val sendIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TITLE, getString(R.string.share_product))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message_template, shareLink))
                type = "text/plain"
            }

            val shareIntent = Intent.createChooser(sendIntent, null)
            activityResultLauncher.launch(shareIntent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            checkProductCode(intent)
        }
    }

    private fun checkProductCode(intent: Intent) {
        val productShop = intent.getStringExtra("productShop")
        val productCode = intent.getStringExtra("productCode")
        val deepLink = intent.data
        val productShopFromDeepLink = deepLink?.getQueryParameter("store")
        val productCodeFromDeepLink = deepLink?.getQueryParameter("code")

        if (productShop != null && productCode != null) {
            productDetailViewModel.productShop = productShop
            productDetailViewModel.productCode = productCode
            productDetailViewModel.getDetails(false)
            return
        }

        if (productShopFromDeepLink != null && productCodeFromDeepLink != null) {
            productDetailViewModel.productShop = productShopFromDeepLink
            productDetailViewModel.productCode = productCodeFromDeepLink
            productDetailViewModel.getDetails(false)
            return
        }

        // 유효하지 않은 경우
        showConfirmDialog(
            getString(R.string.error),
            getString(R.string.invalid_access),
            DialogConfirmAction.FINISH
        )
    }

    private fun observeEvent() {
        repeatOnStarted {
            productDetailViewModel.state.collect { state ->
                state.targetPrice ?: return@collect
                state.shop ?: return@collect
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
                binding.setShopLogoIcon(state.shop)
            }
        }
        repeatOnStarted {
            productDetailViewModel.event.collect { event ->
                when (event) {
                    ProductDetailViewModel.ProductDetailEvent.Logout -> {
                        showDialogWithLogout()
                    }

                    ProductDetailViewModel.ProductDetailEvent.NotFound -> {
                        showConfirmDialog(
                            getString(R.string.error),
                            getString(R.string.product_not_found),
                            DialogConfirmAction.FINISH
                        )
                    }

                    ProductDetailViewModel.ProductDetailEvent.UnknownError -> {
                        showConfirmDialog(
                            getString(R.string.error),
                            getString(R.string.undefined_error),
                            DialogConfirmAction.FINISH
                        )
                    }

                    is ProductDetailViewModel.ProductDetailEvent.OpenShoppingMall -> {
                        launchShopApplication(event.url, event.shop)
                    }

                    ProductDetailViewModel.ProductDetailEvent.DeleteTracking -> {
                        showConfirmationDialogForResult()
                    }

                    is ProductDetailViewModel.ProductDetailEvent.DeleteFailed -> {
                        when (event.errorType) {
                            ProductErrorState.NOT_FOUND -> {
                                showConfirmDialog(
                                    getString(R.string.delete_product_failed),
                                    getString(R.string.product_not_found)
                                )
                            }

                            ProductErrorState.INVALID_REQUEST -> {
                                showConfirmDialog(
                                    getString(R.string.delete_product_failed),
                                    getString(R.string.invalid_request)
                                )
                            }

                            ProductErrorState.PERMISSION_DENIED -> {
                                showDialogWithLogout()
                            }

                            else -> {
                                showConfirmDialog(
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

    private fun ActivityDetailBinding.setShopLogoIcon(shop: String) {
        val iconDrawable = when (shop) {
            "11번가" -> {
                getDrawable(this@DetailActivity, R.drawable.ic_11st_logo)
            }

            "SmartStore", "BrandStore" -> {
                getDrawable(this@DetailActivity, R.drawable.ic_naver_logo)
            }

            else -> return
        }
        ivDetailShoppingMallIcon.setImageDrawable(iconDrawable)
    }

    private fun launchShopApplication(url: String, shop: String) {
        val redirectUrl: String = when (shop) {
            "11번가" -> {
                "elevenst://loadurl?domain=m.11st.co.kr&url=$url&appLnkWyCd=02&domain=m.11st.co.kr&trTypeCd=null"
            }

            "SmartStore", "BrandStore" -> {
                "naversearchapp://inappbrowser?url=$url&target=new&version=6"
            }

            else -> return
        }
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(redirectUrl))
            startActivity(browserIntent)
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            Log.e("LaunchShop", "Launch Shop failed: $e")
            showToast(getString(R.string.failed_to_open_shop))
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
        if (intent.data?.getQueryParameter("code") != null || intent.getBooleanExtra(
                "directed",
                false
            )
        ) {
            val intent = Intent(this@DetailActivity, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        finish()
    }

    private fun showConfirmationDialogForResult() {
        val tag = "confirm_dialog_fragment_from_activity"
        if (supportFragmentManager.findFragmentByTag(tag) != null) return

        val dialogFragment = ConfirmDialogFragment()
        val bundle = Bundle()
        bundle.putString("title", getString(R.string.stop_tracking_confirm))
        bundle.putString("message", getString(R.string.stop_tracking_detail))
        bundle.putString("actionString", DialogConfirmAction.CUSTOM.name)
        dialogFragment.arguments = bundle
        dialogFragment.setOnDialogResultListener(this)
        dialogFragment.show(supportFragmentManager, tag)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onDialogResult(result: Boolean) {
        if (result) {
            productDetailViewModel.deleteProductTracking()
        }
    }
}

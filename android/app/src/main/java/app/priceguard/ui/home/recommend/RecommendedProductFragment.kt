package app.priceguard.ui.home.recommend

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.priceguard.R
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.databinding.FragmentRecommendedProductBinding
import app.priceguard.ui.detail.DetailActivity
import app.priceguard.ui.home.ProductSummaryAdapter
import app.priceguard.ui.home.ProductSummaryClickListener
import app.priceguard.ui.util.drawable.getCircularProgressIndicatorDrawable
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.showDialogWithAction
import app.priceguard.ui.util.showDialogWithLogout
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecommendedProductFragment : Fragment() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    private var _binding: FragmentRecommendedProductBinding? = null
    private val binding get() = _binding!!
    private val recommendedProductViewModel: RecommendedProductViewModel by viewModels()

    private lateinit var circularProgressIndicator: IndeterminateDrawable<CircularProgressIndicatorSpec>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendedProductBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        circularProgressIndicator = getCircularProgressIndicatorDrawable(view.context, R.style.Theme_PriceGuard_CircularProgressLoading)
        binding.viewModel = recommendedProductViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.initSettingAdapter()
        binding.initListener()
        collectEvent()
    }

    override fun onStart() {
        super.onStart()
        recommendedProductViewModel.getRecommendedProductList(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        circularProgressIndicator.stop()
    }

    private fun FragmentRecommendedProductBinding.initSettingAdapter() {
        val listener = object : ProductSummaryClickListener {
            override fun onClick(productCode: String) {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("productCode", productCode)
                startActivity(intent)
            }

            override fun onToggle(productCode: String, checked: Boolean) {
                return
            }
        }

        val adapter = ProductSummaryAdapter(listener, ProductSummaryAdapter.diffUtil)
        rvRecommendedProduct.adapter = adapter
        this@RecommendedProductFragment.repeatOnStarted {
            recommendedProductViewModel.state.collect { state ->
                if (state.recommendedList.isEmpty()) {
                    binding.loadingLayoutRecommendedProduct.visibility = View.VISIBLE
                    binding.rvRecommendedProduct.visibility = View.GONE
                    if (state.isUpdated) {
                        binding.noProductRecommendedProduct.visibility = View.VISIBLE
                        binding.loadingSpinnerRecommendedProduct.visibility = View.GONE
                    } else {
                        binding.noProductRecommendedProduct.visibility = View.GONE
                        binding.loadingSpinnerRecommendedProduct.visibility = View.VISIBLE
                        binding.loadingSpinnerRecommendedProduct.setImageDrawable(circularProgressIndicator)
                    }
                } else {
                    binding.loadingLayoutRecommendedProduct.visibility = View.GONE
                    binding.rvRecommendedProduct.visibility = View.VISIBLE
                    adapter.submitList(state.recommendedList)
                }
            }
        }
    }

    private fun FragmentRecommendedProductBinding.initListener() {
        ablRecommendedProduct.addOnOffsetChangedListener { _, verticalOffset ->
            srlRecommendedProduct.isEnabled = verticalOffset == 0
        }
    }

    private fun collectEvent() {
        repeatOnStarted {
            recommendedProductViewModel.events.collect { event ->
                when (event) {
                    ProductErrorState.PERMISSION_DENIED -> {
                        showDialogWithLogout()
                    }

                    ProductErrorState.INVALID_REQUEST -> {
                        showDialogWithAction(
                            getString(R.string.recommended_product_failed),
                            getString(R.string.invalid_request)
                        )
                    }

                    ProductErrorState.NOT_FOUND -> {
                        showDialogWithAction(
                            getString(R.string.recommended_product_failed),
                            getString(R.string.not_found)
                        )
                    }

                    else -> {
                        showDialogWithAction(
                            getString(R.string.recommended_product_failed),
                            getString(R.string.undefined_error)
                        )
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvRecommendedProduct.adapter = null
        _binding = null
    }
}

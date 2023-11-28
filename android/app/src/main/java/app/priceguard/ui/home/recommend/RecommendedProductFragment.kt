package app.priceguard.ui.home.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.priceguard.R
import app.priceguard.data.dto.ProductErrorState
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.FragmentRecommendedProductBinding
import app.priceguard.ui.home.ProductSummaryAdapter
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.ui.disableAppBarRecyclerView
import app.priceguard.ui.util.ui.showConfirmationDialog
import app.priceguard.ui.util.ui.showPermissionDeniedDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RecommendedProductFragment : Fragment() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    private var _binding: FragmentRecommendedProductBinding? = null
    private val binding get() = _binding!!
    private val recommendedProductViewModel: RecommendedProductViewModel by viewModels()

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
        binding.viewModel = recommendedProductViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.initSettingAdapter()
        binding.initListener()
        collectEvent()
        disableAppBarRecyclerView(
            binding.ablRecommendedProduct.layoutParams as CoordinatorLayout.LayoutParams,
            binding.rvRecommendedProduct
        )
    }

    override fun onStart() {
        super.onStart()
        recommendedProductViewModel.getRecommendedProductList(false)
    }

    private fun FragmentRecommendedProductBinding.initSettingAdapter() {
        val adapter = ProductSummaryAdapter()
        rvRecommendedProduct.adapter = adapter
        this@RecommendedProductFragment.repeatOnStarted {
            recommendedProductViewModel.recommendedProductList.collect { list ->
                adapter.submitList(list)
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
                        activity?.showPermissionDeniedDialog(tokenRepository)
                    }

                    ProductErrorState.INVALID_REQUEST -> {
                        activity?.showConfirmationDialog(
                            getString(R.string.recommended_product_failed),
                            getString(R.string.invalid_request)
                        )
                    }

                    ProductErrorState.NOT_FOUND -> {
                        activity?.showConfirmationDialog(
                            getString(R.string.recommended_product_failed),
                            getString(R.string.not_found)
                        )
                    }

                    else -> {
                        activity?.showConfirmationDialog(
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
        _binding = null
    }
}

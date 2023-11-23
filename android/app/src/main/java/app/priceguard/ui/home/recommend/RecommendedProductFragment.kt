package app.priceguard.ui.home.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.priceguard.R
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.FragmentRecommendedProductBinding
import app.priceguard.ui.home.ProductSummaryAdapter
import app.priceguard.ui.home.recommend.RecommendedProductViewModel.RecommendedProductEvent
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.ui.disableAppBarRecyclerView
import app.priceguard.ui.util.ui.showNetworkDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

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
        mtbRecommendedProduct.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.refresh) {
                lifecycleScope.launch {
                    recommendedProductViewModel.getProductList()
                }
            }
            true
        }
    }

    private fun collectEvent() {
        repeatOnStarted {
            recommendedProductViewModel.events.collect { event ->
                if (event is RecommendedProductEvent.PermissionDenied) {
                    activity?.showNetworkDialog(tokenRepository)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

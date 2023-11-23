package app.priceguard.ui.home.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.priceguard.R
import app.priceguard.databinding.FragmentRecommendedProductBinding
import app.priceguard.ui.home.ProductSummaryAdapter
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecommendedProductFragment : Fragment() {

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
    }

    private fun FragmentRecommendedProductBinding.initSettingAdapter() {
        val adapter = ProductSummaryAdapter()
        rvMyPageSetting.adapter = adapter
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package app.priceguard.ui.home.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.priceguard.databinding.FragmentRecommendedProductBinding
import app.priceguard.ui.home.ProductSummaryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RecommendedProductFragment : Fragment() {

    private var _binding: FragmentRecommendedProductBinding? = null
    private val binding get() = _binding!!

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

        initSettingAdapter()
    }

    private fun initSettingAdapter() {
        binding.rvMyPageSetting.adapter = ProductSummaryAdapter(listOf())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

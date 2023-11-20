package app.priceguard.ui.home.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.priceguard.databinding.FragmentProductListBinding
import app.priceguard.ui.home.ProductSummaryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductListBinding.inflate(layoutInflater, container, false)
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

package app.priceguard.ui.home.list

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.priceguard.R
import app.priceguard.databinding.FragmentProductListBinding
import app.priceguard.ui.home.ProductSummaryAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val productListViewModel: ProductListViewModel by viewModels()

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
        initListener()
    }

    private fun initSettingAdapter() {
        binding.rvMyPageSetting.adapter = ProductSummaryAdapter(productListViewModel.list.value)
    }

    private fun initListener() {
        with(binding) {
            mtbProductList.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.refresh -> {
                        productListViewModel.refreshScreen()
                        true
                    }

                    else -> {
                        true
                    }
                }
            }
            fabProductList.setOnClickListener {
                Log.d("TEST", "add")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

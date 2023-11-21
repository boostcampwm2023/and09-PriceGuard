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
import app.priceguard.ui.util.lifecycle.repeatOnStarted
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
        binding.lifecycleOwner = viewLifecycleOwner
        binding.initSettingAdapter()
        binding.initListener()
    }

    private fun FragmentProductListBinding.initSettingAdapter() {
        val adapter = ProductSummaryAdapter()
        rvMyPageSetting.adapter = adapter
        lifecycleOwner?.repeatOnStarted {
            productListViewModel.list.collect { list ->
                adapter.submitList(list)
            }
        }
    }

    private fun FragmentProductListBinding.initListener() {
        mtbProductList.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.refresh -> {
                    productListViewModel.getProductList()
                    true
                }

                else -> {
                    true
                }
            }
        }
        fabProductList.setOnClickListener {
            // TODO: 상품 추가 화면 이동
            Log.d("TEST", "add")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

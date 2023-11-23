package app.priceguard.ui.home.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import app.priceguard.R
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.FragmentProductListBinding
import app.priceguard.ui.home.ProductSummaryAdapter
import app.priceguard.ui.home.list.ProductListViewModel.ProductListEvent
import app.priceguard.ui.main.additem.AddItemActivity
import app.priceguard.ui.util.drawable.showNetworkDialog
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    @Inject
    lateinit var tokenRepository: TokenRepository

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
        binding.viewModel = productListViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.initSettingAdapter()
        binding.initListener()
        collectEvent()
    }

    private fun FragmentProductListBinding.initSettingAdapter() {
        val adapter = ProductSummaryAdapter()
        rvMyPageSetting.adapter = adapter
        this@ProductListFragment.repeatOnStarted {
            productListViewModel.productList.collect { list ->
                adapter.submitList(list)
            }
        }
    }

    private fun FragmentProductListBinding.initListener() {
        mtbProductList.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.refresh) {
                lifecycleScope.launch {
                    productListViewModel.getProductList()
                }
            }
            true
        }
        fabProductList.setOnClickListener {
            gotoProductAddActivity()
        }
    }

    private fun gotoProductAddActivity() {
        val intent = Intent(activity, AddItemActivity::class.java)
        startActivity(intent)
    }

    private fun collectEvent() {
        repeatOnStarted {
            productListViewModel.events.collect { event ->
                if (event is ProductListEvent.PermissionDenied) {
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

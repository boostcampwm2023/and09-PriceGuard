package app.priceguard.ui.home.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.FragmentProductListBinding
import app.priceguard.ui.additem.AddItemActivity
import app.priceguard.ui.home.ProductSummaryAdapter
import app.priceguard.ui.home.list.ProductListViewModel.ProductListEvent
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.ui.disableAppBarRecyclerView
import app.priceguard.ui.util.ui.showPermissionDeniedDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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
        disableAppBarRecyclerView(
            binding.ablProductList.layoutParams as CoordinatorLayout.LayoutParams,
            binding.rvProductList
        )
    }

    override fun onStart() {
        super.onStart()
        if (productListViewModel.isReady.value) {
            productListViewModel.getProductList(false)
        }
    }

    private fun FragmentProductListBinding.initSettingAdapter() {
        val adapter = ProductSummaryAdapter()
        rvProductList.adapter = adapter
        this@ProductListFragment.repeatOnStarted {
            productListViewModel.productList.collect { list ->
                adapter.submitList(list)
            }
        }
    }

    private fun FragmentProductListBinding.initListener() {
        fabProductList.setOnClickListener {
            gotoProductAddActivity()
        }

        ablProductList.addOnOffsetChangedListener { _, verticalOffset ->
            srlProductList.isEnabled = verticalOffset == 0
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
                    activity?.showPermissionDeniedDialog(tokenRepository)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package app.priceguard.ui.home.list

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.WorkManager
import app.priceguard.R
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.databinding.FragmentProductListBinding
import app.priceguard.service.UpdateAlarmWorker
import app.priceguard.ui.additem.AddItemActivity
import app.priceguard.ui.detail.DetailActivity
import app.priceguard.ui.home.ProductSummaryAdapter
import app.priceguard.ui.home.ProductSummaryClickListener
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.showDialogWithAction
import app.priceguard.ui.util.showDialogWithLogout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProductListFragment : Fragment() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    private var _binding: FragmentProductListBinding? = null
    private val binding get() = _binding!!
    private val productListViewModel: ProductListViewModel by viewModels()

    private var workRequestSet: MutableSet<Pair<String, String>> = mutableSetOf()

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

    override fun onStart() {
        super.onStart()
        productListViewModel.getProductList(false)
    }

    private fun FragmentProductListBinding.initSettingAdapter() {
        val listener = object : ProductSummaryClickListener {
            override fun onClick(productShop: String, productCode: String) {
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("productShop", productShop)
                intent.putExtra("productCode", productCode)
                startActivity(intent)
            }

            override fun onToggle(productShop: String, productCode: String, checked: Boolean) {
                productListViewModel.updateProductAlarmToggle(productShop, productCode, checked)
                if (workRequestSet.contains(Pair(productShop, productCode))) {
                    workRequestSet.remove(Pair(productShop, productCode))
                } else {
                    workRequestSet.add(Pair(productShop, productCode))
                }
            }
        }

        val adapter = ProductSummaryAdapter(listener, ProductSummaryAdapter.userDiffUtil)
        rvProductList.adapter = adapter
        this@ProductListFragment.repeatOnStarted {
            productListViewModel.state.collect { state ->
                if (state.productList.isNotEmpty()) {
                    adapter.submitList(state.productList)
                }
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
        viewLifecycleOwner.repeatOnStarted {
            productListViewModel.events.collect { event ->
                when (event) {
                    ProductErrorState.PERMISSION_DENIED -> {
                        showDialogWithLogout()
                    }

                    ProductErrorState.INVALID_REQUEST -> {
                        showDialogWithAction(
                            getString(R.string.product_list_failed),
                            getString(R.string.invalid_request)
                        )
                    }

                    ProductErrorState.NOT_FOUND -> {
                        showDialogWithAction(
                            getString(R.string.product_list_failed),
                            getString(R.string.not_found)
                        )
                    }

                    else -> {
                        showDialogWithAction(
                            getString(R.string.product_list_failed),
                            getString(R.string.undefined_error)
                        )
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        workRequestSet.forEach { requestData ->
            WorkManager.getInstance(requireContext())
                .enqueue(UpdateAlarmWorker.createWorkRequest(requestData))
        }
        workRequestSet.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.rvProductList.adapter = null
        _binding = null
    }
}

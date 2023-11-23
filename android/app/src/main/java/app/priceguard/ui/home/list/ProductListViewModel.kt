package app.priceguard.ui.home.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.ProductRepository
import app.priceguard.ui.home.ProductSummary.UserProductSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private var _productList = MutableStateFlow<List<UserProductSummary>>(listOf())
    val productList: StateFlow<List<UserProductSummary>> = _productList.asStateFlow()

    init {
        viewModelScope.launch {
            getProductList()
        }
    }

    suspend fun getProductList() {
        val list = productRepository.getProductList()
        _productList.value = list.trackingList.map { data ->
            UserProductSummary(
                data.shop,
                data.productName,
                "",
                "",
                data.productCode,
                true
            )
        }
    }
}

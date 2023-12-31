package app.priceguard.ui.home.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.GraphDataConverter
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.product.ProductRepository
import app.priceguard.materialchart.data.GraphMode
import app.priceguard.ui.data.ProductData
import app.priceguard.ui.home.ProductSummary.UserProductSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.round
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val graphDataConverter: GraphDataConverter
) : ViewModel() {

    private var _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var _productList = MutableStateFlow<List<UserProductSummary>>(listOf())
    val productList: StateFlow<List<UserProductSummary>> = _productList.asStateFlow()

    private var _events = MutableSharedFlow<ProductErrorState>()
    val events: SharedFlow<ProductErrorState> = _events.asSharedFlow()

    fun getProductList(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            }

            val result = productRepository.getProductList()

            _isRefreshing.value = false

            when (result) {
                is RepositoryResult.Success -> {
                    updateProductList(isRefresh, result.data)
                }

                is RepositoryResult.Error -> {
                    _events.emit(result.errorState)
                }
            }
        }
    }

    private fun updateProductList(refresh: Boolean, fetched: List<ProductData>) {
        val productMap = mutableMapOf<String, Boolean>()
        _productList.value.forEach { product ->
            productMap[product.productCode] = product.isAlarmOn
        }

        _productList.value = fetched.map { data ->
            UserProductSummary(
                data.shop,
                data.productName,
                data.price,
                data.productCode,
                graphDataConverter.packWithEdgeData(data.priceData, GraphMode.WEEK),
                calculateDiscountRate(data.targetPrice, data.price),
                if (refresh) productMap[data.productCode] ?: data.isAlert else data.isAlert
            )
        }
    }

    fun updateProductAlarmToggle(productCode: String, checked: Boolean) {
        _productList.value = productList.value.mapIndexed { _, product ->
            if (product.productCode == productCode) {
                product.copy(isAlarmOn = checked)
            } else {
                product
            }
        }
    }

    private fun calculateDiscountRate(targetPrice: Int, price: Int): Float {
        return round((price - targetPrice).toFloat() / (if (price == 0) 1 else price) * 1000) / 10
    }
}

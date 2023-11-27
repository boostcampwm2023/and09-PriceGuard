package app.priceguard.ui.home.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.ProductListState
import app.priceguard.data.repository.ProductRepository
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
    private val productRepository: ProductRepository
) : ViewModel() {

    sealed class ProductListEvent {
        data object PermissionDenied : ProductListEvent()
    }

    private var _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var _productList = MutableStateFlow<List<UserProductSummary>>(listOf())
    val productList: StateFlow<List<UserProductSummary>> = _productList.asStateFlow()

    private var _events = MutableSharedFlow<ProductListEvent>()
    val events: SharedFlow<ProductListEvent> = _events.asSharedFlow()

    fun getProductList(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            }

            val result = productRepository.getProductList()

            _isRefreshing.value = false

            if (result.productListState == ProductListState.PERMISSION_DENIED) {
                _events.emit(ProductListEvent.PermissionDenied)
            } else {
                _productList.value = result.trackingList.map { data ->
                    UserProductSummary(
                        data.shop,
                        data.productName,
                        data.price,
                        data.productCode,
                        calculateDiscountRate(data.targetPrice, data.price),
                        true
                    )
                }
            }
        }
    }

    private fun calculateDiscountRate(targetPrice: Int, price: Int): Float {
        return round((price - targetPrice).toFloat() / (if (targetPrice == 0) 1 else targetPrice) * 1000) / 10
    }
}

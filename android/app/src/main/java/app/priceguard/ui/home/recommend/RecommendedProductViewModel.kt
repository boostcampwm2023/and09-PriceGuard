package app.priceguard.ui.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.RecommendProductState
import app.priceguard.data.repository.ProductRepository
import app.priceguard.ui.home.ProductSummary.RecommendedProductSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RecommendedProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    sealed class RecommendedProductEvent {
        data object PermissionDenied : RecommendedProductEvent()
    }

    private var _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private var _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var _recommendedProductList =
        MutableStateFlow<List<RecommendedProductSummary>>(listOf())
    val recommendedProductList: StateFlow<List<RecommendedProductSummary>> =
        _recommendedProductList.asStateFlow()

    private var _events = MutableSharedFlow<RecommendedProductEvent>()
    val events: SharedFlow<RecommendedProductEvent> = _events.asSharedFlow()

    init {
        getRecommendedProductList(false)
    }

    fun getRecommendedProductList(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            }

            val result = productRepository.getRecommendedProductList()
            _isRefreshing.value = false
            _isReady.value = true

            if (result.productListState == RecommendProductState.PERMISSION_DENIED) {
                _events.emit(RecommendedProductEvent.PermissionDenied)
            } else {
                _recommendedProductList.value = result.recommendList.map { data ->
                    RecommendedProductSummary(
                        data.shop,
                        data.productName,
                        data.price.toString(),
                        "-15.3%",
                        data.productCode,
                        data.rank
                    )
                }
            }
        }
    }
}

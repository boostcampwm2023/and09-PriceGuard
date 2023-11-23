package app.priceguard.ui.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.ProductListState
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

    private var _recommendedProductList =
        MutableStateFlow<List<RecommendedProductSummary>>(listOf())
    val recommendedProductList: StateFlow<List<RecommendedProductSummary>> =
        _recommendedProductList.asStateFlow()

    private var _events = MutableSharedFlow<RecommendedProductEvent>()
    val events: SharedFlow<RecommendedProductEvent> = _events.asSharedFlow()

    init {
        viewModelScope.launch {
            getProductList()
        }
    }

    suspend fun getProductList() {
        val result = productRepository.getRecommendedProductList()
        if (result.productListState == ProductListState.PERMISSION_DENIED) {
            _events.emit(RecommendedProductEvent.PermissionDenied)
        } else {
            _recommendedProductList.value = result.trackingList.map { data ->
                RecommendedProductSummary(
                    data.shop,
                    data.productName,
                    "",
                    "",
                    data.productCode,
                    1
                )
            }
        }
    }
}

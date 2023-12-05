package app.priceguard.ui.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.ProductErrorState
import app.priceguard.data.graph.GraphDataConverter
import app.priceguard.data.network.ProductRepositoryResult
import app.priceguard.data.repository.ProductRepository
import app.priceguard.materialchart.data.GraphMode
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
    private val productRepository: ProductRepository,
    private val graphDataConverter: GraphDataConverter
) : ViewModel() {

    private var _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var _recommendedProductList =
        MutableStateFlow<List<RecommendedProductSummary>>(listOf())
    val recommendedProductList: StateFlow<List<RecommendedProductSummary>> =
        _recommendedProductList.asStateFlow()

    private var _events = MutableSharedFlow<ProductErrorState>()
    val events: SharedFlow<ProductErrorState> = _events.asSharedFlow()

    fun getRecommendedProductList(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            }

            val result = productRepository.getRecommendedProductList()
            _isRefreshing.value = false

            when (result) {
                is ProductRepositoryResult.Success -> {
                    _recommendedProductList.value = result.data.map { data ->
                        RecommendedProductSummary(
                            data.shop,
                            data.productName,
                            data.price,
                            data.productCode,
                            graphDataConverter.packWithEdgeData(data.priceData, GraphMode.WEEK),
                            data.rank
                        )
                    }
                }

                is ProductRepositoryResult.Error -> {
                    _events.emit(result.productErrorState)
                }
            }
        }
    }
}

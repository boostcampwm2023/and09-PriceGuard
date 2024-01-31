package app.priceguard.ui.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.GraphDataConverter
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.product.ProductRepository
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

    data class State(
        val isRefreshing: Boolean = false,
        val isUpdated: Boolean = false,
        val recommendedList: List<RecommendedProductSummary> = listOf()
    )

    private var _state: MutableStateFlow<State> = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    private var _events = MutableSharedFlow<ProductErrorState>()
    val events: SharedFlow<ProductErrorState> = _events.asSharedFlow()

    fun getRecommendedProductList(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _state.value = _state.value.copy(isRefreshing = true)
            }
            _state.value = _state.value.copy(isUpdated = false)
            val result = productRepository.getRecommendedProductList()

            _state.value = _state.value.copy(isRefreshing = false)

            when (result) {
                is RepositoryResult.Success -> {
                    _state.value = _state.value.copy(
                        recommendedList = result.data.map { data ->
                            RecommendedProductSummary(
                                data.shop,
                                data.productName,
                                data.price,
                                data.productCode,
                                graphDataConverter.packWithEdgeData(data.priceData, GraphMode.WEEK),
                                data.rank
                            )
                        }
                    )
                }

                is RepositoryResult.Error -> {
                    _events.emit(result.errorState)
                }
            }
        }
    }
}

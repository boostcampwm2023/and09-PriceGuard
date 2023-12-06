package app.priceguard.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.GraphDataConverter
import app.priceguard.data.graph.ProductChartData
import app.priceguard.data.repository.ProductErrorState
import app.priceguard.data.network.ProductRepositoryResult
import app.priceguard.data.repository.ProductRepository
import app.priceguard.data.repository.ProductRepositoryResult
import app.priceguard.materialchart.data.GraphMode
import dagger.hilt.android.lifecycle.HiltViewModel
import java.text.NumberFormat
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val graphDataConverter: GraphDataConverter
) : ViewModel() {

    data class ProductDetailUIState(
        val isTracking: Boolean = false,
        val isReady: Boolean = false,
        val isRefreshing: Boolean = false,
        val productName: String? = null,
        val shop: String? = null,
        val imageUrl: String? = null,
        val rank: Int? = null,
        val shopUrl: String? = null,
        val targetPrice: Int? = null,
        val lowestPrice: Int? = null,
        val price: Int? = null,
        val formattedPrice: String = "",
        val formattedTargetPrice: String = "",
        val formattedLowestPrice: String = "",
        val graphMode: GraphMode = GraphMode.DAY,
        val chartData: List<ProductChartData> = listOf()
    )

    sealed class ProductDetailEvent {
        data class OpenShoppingMall(val url: String) : ProductDetailEvent()
        data object DeleteTracking : ProductDetailEvent()
        data object Logout : ProductDetailEvent()
        data object NotFound : ProductDetailEvent()
        data object UnknownError : ProductDetailEvent()
        data object DeleteSuccess : ProductDetailEvent()
        data class DeleteFailed(val errorType: ProductErrorState) : ProductDetailEvent()
    }

    lateinit var productCode: String
    private var productGraphData: List<ProductChartData> = listOf()

    private var _event: MutableSharedFlow<ProductDetailEvent> = MutableSharedFlow()
    val event: SharedFlow<ProductDetailEvent> = _event.asSharedFlow()

    private var _state: MutableStateFlow<ProductDetailUIState> =
        MutableStateFlow(ProductDetailUIState())
    val state: StateFlow<ProductDetailUIState> = _state.asStateFlow()

    fun deleteProductTracking() {
        viewModelScope.launch {
            when (val result = productRepository.deleteProduct(productCode)) {
                is ProductRepositoryResult.Success -> {
                    _event.emit(ProductDetailEvent.DeleteSuccess)
                }

                is ProductRepositoryResult.Error -> {
                    _event.emit(ProductDetailEvent.DeleteFailed(result.productErrorState))
                }
            }
        }
    }

    fun getDetails(isRefresh: Boolean) {
        viewModelScope.launch {
            if (::productCode.isInitialized.not()) {
                return@launch
            }

            if (isRefresh) {
                _state.value = _state.value.copy(isRefreshing = true)
            }

            val result = productRepository.getProductDetail(productCode)

            _state.value = _state.value.copy(isRefreshing = false)

            when (result) {
                is ProductRepositoryResult.Success -> {
                    productGraphData = result.data.priceData
                    _state.update {
                        it.copy(
                            isReady = true,
                            isTracking = result.data.targetPrice >= 0,
                            productName = result.data.productName,
                            shop = result.data.shop,
                            imageUrl = result.data.imageUrl,
                            rank = result.data.rank,
                            shopUrl = result.data.shopUrl,
                            targetPrice = result.data.targetPrice,
                            lowestPrice = result.data.lowestPrice,
                            price = result.data.price,
                            formattedPrice = formatPrice(result.data.price),
                            formattedTargetPrice = if (result.data.targetPrice < 0) {
                                "0"
                            } else {
                                formatPrice(
                                    result.data.targetPrice
                                )
                            },
                            formattedLowestPrice = formatPrice(result.data.lowestPrice),
                            chartData = graphDataConverter.packWithEdgeData(
                                result.data.priceData,
                                state.value.graphMode
                            )
                        )
                    }
                }

                is ProductRepositoryResult.Error -> {
                    when (result.productErrorState) {
                        ProductErrorState.PERMISSION_DENIED -> {
                            _event.emit(ProductDetailEvent.Logout)
                        }

                        ProductErrorState.NOT_FOUND -> {
                            _event.emit(ProductDetailEvent.NotFound)
                        }

                        else -> {
                            _event.emit(ProductDetailEvent.UnknownError)
                        }
                    }
                }
            }
        }
    }

    fun changePeriod(period: GraphMode) {
        if (productGraphData.isEmpty()) {
            return
        }

        _state.update {
            it.copy(
                graphMode = period,
                chartData = graphDataConverter.packWithEdgeData(productGraphData, period)
            )
        }
    }

    fun sendBrowserEvent() {
        viewModelScope.launch {
            val event = _state.value.shopUrl?.let { ProductDetailEvent.OpenShoppingMall(it) }
                ?: return@launch
            _event.emit(event)
        }
    }

    fun sendDeleteTrackingEvent() {
        viewModelScope.launch {
            _event.emit(ProductDetailEvent.DeleteTracking)
        }
    }

    private fun formatPrice(price: Int): String {
        return NumberFormat.getNumberInstance().format(price)
    }
}

package app.priceguard.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.GraphDataConverter
import app.priceguard.data.graph.ProductChartData
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.product.ProductRepository
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
        data class OpenShoppingMall(val url: String, val shop: String) : ProductDetailEvent()
        data object DeleteTracking : ProductDetailEvent()
        data object Logout : ProductDetailEvent()
        data object NotFound : ProductDetailEvent()
        data object UnknownError : ProductDetailEvent()
        data object DeleteSuccess : ProductDetailEvent()
        data class DeleteFailed(val errorType: ProductErrorState) : ProductDetailEvent()
    }

    lateinit var productShop: String
    lateinit var productCode: String
    private var productGraphData: List<ProductChartData> = listOf()

    private var _event: MutableSharedFlow<ProductDetailEvent> = MutableSharedFlow()
    val event: SharedFlow<ProductDetailEvent> = _event.asSharedFlow()

    private var _state: MutableStateFlow<ProductDetailUIState> =
        MutableStateFlow(ProductDetailUIState())
    val state: StateFlow<ProductDetailUIState> = _state.asStateFlow()

    fun deleteProductTracking() {
        viewModelScope.launch {
            when (val result = productRepository.deleteProduct(productShop, productCode)) {
                is RepositoryResult.Success -> {
                    _event.emit(ProductDetailEvent.DeleteSuccess)
                }

                is RepositoryResult.Error -> {
                    _event.emit(ProductDetailEvent.DeleteFailed(result.errorState))
                }
            }
        }
    }

    fun getDetails(isRefresh: Boolean) {
        viewModelScope.launch {
            if (::productCode.isInitialized.not() && _state.value.shop == null) {
                return@launch
            }

            if (isRefresh) {
                _state.value = _state.value.copy(isRefreshing = true)
            }

            val result = productRepository.getProductDetail(productShop, productCode)

            _state.value = _state.value.copy(isRefreshing = false)

            when (result) {
                is RepositoryResult.Success -> {
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

                is RepositoryResult.Error -> {
                    when (result.errorState) {
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
            val event = _state.value.let {
                if (it.shopUrl == null || it.shop == null) return@launch
                ProductDetailEvent.OpenShoppingMall(it.shopUrl, it.shop)
            }
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

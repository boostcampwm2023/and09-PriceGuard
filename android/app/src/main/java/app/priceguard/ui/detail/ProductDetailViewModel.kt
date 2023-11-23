package app.priceguard.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.ProductDetailState
import app.priceguard.data.repository.ProductRepository
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
class ProductDetailViewModel @Inject constructor(val productRepository: ProductRepository) :
    ViewModel() {

    data class ProductDetailUIState(
        val isTracking: Boolean = false,
        val isReady: Boolean = false,
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
        val formattedLowestPrice: String = ""
    )

    sealed class ProductDetailEvent {
        data class OpenShoppingMall(val url: String) : ProductDetailEvent()
        data object Logout : ProductDetailEvent()
        data object NotFound : ProductDetailEvent()
        data object UnknownError : ProductDetailEvent()
    }

    lateinit var productCode: String

    private var _event: MutableSharedFlow<ProductDetailEvent> = MutableSharedFlow()
    val event: SharedFlow<ProductDetailEvent> = _event.asSharedFlow()

    private var _state: MutableStateFlow<ProductDetailUIState> =
        MutableStateFlow(ProductDetailUIState())
    val state: StateFlow<ProductDetailUIState> = _state.asStateFlow()

    fun getDetails() {
        viewModelScope.launch {
            if (::productCode.isInitialized.not()) {
                return@launch
            }

            val result = productRepository.getProductDetail(productCode, false)

            when (result.state) {
                ProductDetailState.SUCCESS -> {
                    if (result.productName == null ||
                        result.productCode == null ||
                        result.shop == null ||
                        result.imageUrl == null ||
                        result.rank == null ||
                        result.shopUrl == null ||
                        result.targetPrice == null ||
                        result.lowestPrice == null ||
                        result.price == null
                    ) {
                        _event.emit(ProductDetailEvent.UnknownError)
                        return@launch
                    }

                    _state.update {
                        it.copy(
                            isReady = true,
                            isTracking = result.targetPrice >= 0,
                            productName = result.productName,
                            shop = result.shop,
                            imageUrl = result.imageUrl,
                            rank = result.rank,
                            shopUrl = result.shopUrl,
                            targetPrice = result.targetPrice,
                            lowestPrice = result.lowestPrice,
                            price = result.price,
                            formattedPrice = formatPrice(result.price),
                            formattedTargetPrice = if (result.targetPrice < 0) {
                                "0"
                            } else {
                                formatPrice(
                                    result.targetPrice
                                )
                            },
                            formattedLowestPrice = formatPrice(result.lowestPrice)
                        )
                    }
                }

                ProductDetailState.PERMISSION_DENIED -> {
                    _event.emit(ProductDetailEvent.Logout)
                }

                ProductDetailState.NOT_FOUND -> {
                    _event.emit(ProductDetailEvent.NotFound)
                }

                ProductDetailState.UNDEFINED_ERROR -> {
                    _event.emit(ProductDetailEvent.UnknownError)
                }
            }
        }
    }

    fun sendBrowserEvent() {
        viewModelScope.launch {
            val event = _state.value.shopUrl?.let { ProductDetailEvent.OpenShoppingMall(it) } ?: return@launch
            _event.emit(event)
        }
    }

    private fun formatPrice(price: Int): String {
        return NumberFormat.getNumberInstance().format(price)
    }
}

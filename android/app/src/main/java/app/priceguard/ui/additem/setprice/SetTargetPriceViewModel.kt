package app.priceguard.ui.additem.setprice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.ProductErrorState
import app.priceguard.data.dto.add.ProductAddRequest
import app.priceguard.data.dto.patch.PricePatchRequest
import app.priceguard.data.network.ProductRepositoryResult
import app.priceguard.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SetTargetPriceViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    data class SetTargetPriceState(
        val productCode: String = "",
        val targetPrice: Int = 0,
        val productName: String = "",
        val productPrice: Int = 0
    )

    sealed class SetTargetPriceEvent {
        data object SuccessProductAdd : SetTargetPriceEvent()
        data class FailurePriceAdd(val errorType: ProductErrorState) : SetTargetPriceEvent()
        data object SuccessPriceUpdate : SetTargetPriceEvent()
        data class FailurePriceUpdate(val errorType: ProductErrorState) : SetTargetPriceEvent()
    }

    private val _state = MutableStateFlow(SetTargetPriceState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<SetTargetPriceEvent>()
    val event = _event.asSharedFlow()

    fun addProduct() {
        viewModelScope.launch {
            val response = productRepository.addProduct(
                ProductAddRequest(
                    _state.value.productCode,
                    _state.value.targetPrice
                )
            )
            when (response) {
                is ProductRepositoryResult.Success -> {
                    _event.emit(SetTargetPriceEvent.SuccessProductAdd)
                }

                is ProductRepositoryResult.Error -> {
                    _event.emit(SetTargetPriceEvent.FailurePriceAdd(response.productErrorState))
                }
            }
        }
    }

    fun patchProduct() {
        viewModelScope.launch {
            val response = productRepository.updateTargetPrice(
                PricePatchRequest(
                    _state.value.productCode,
                    _state.value.targetPrice
                )
            )
            when (response) {
                is ProductRepositoryResult.Success -> {
                    _event.emit(SetTargetPriceEvent.SuccessPriceUpdate)
                }

                is ProductRepositoryResult.Error -> {
                    _event.emit(SetTargetPriceEvent.FailurePriceUpdate(response.productErrorState))
                }
            }
        }
    }

    fun updateTargetPrice(price: Int) {
        _state.value = state.value.copy(targetPrice = price)
    }

    fun setProductInfo(productCode: String, name: String, price: Int) {
        _state.value =
            state.value.copy(
                productCode = productCode,
                productName = name,
                productPrice = price
            )
    }
}

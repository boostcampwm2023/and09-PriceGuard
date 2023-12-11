package app.priceguard.ui.additem.setprice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.product.ProductRepository
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
        val productPrice: Int = 0,
        val isReady: Boolean = true
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
            _state.value = state.value.copy(isReady = false)
            val response = productRepository.addProduct(
                _state.value.productCode,
                _state.value.targetPrice
            )
            when (response) {
                is RepositoryResult.Success -> {
                    _event.emit(SetTargetPriceEvent.SuccessProductAdd)
                }

                is RepositoryResult.Error -> {
                    _event.emit(SetTargetPriceEvent.FailurePriceAdd(response.errorState))
                }
            }
            _state.value = state.value.copy(isReady = true)
        }
    }

    fun patchProduct() {
        viewModelScope.launch {
            val response = productRepository.updateTargetPrice(
                _state.value.productCode,
                _state.value.targetPrice
            )
            when (response) {
                is RepositoryResult.Success -> {
                    _event.emit(SetTargetPriceEvent.SuccessPriceUpdate)
                }

                is RepositoryResult.Error -> {
                    _event.emit(SetTargetPriceEvent.FailurePriceUpdate(response.errorState))
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

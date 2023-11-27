package app.priceguard.ui.additem.setprice

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.network.APIResult
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
        data object FailureProductAdd : SetTargetPriceEvent()
    }

    private val _state = MutableStateFlow(SetTargetPriceState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<SetTargetPriceEvent>()
    val event = _event.asSharedFlow()

    fun addProduct() {
        Log.d("responseSetTargetPrice", "1")
        viewModelScope.launch {
            val response = productRepository.addProduct(
                ProductAddRequest(
                    _state.value.productCode,
                    _state.value.targetPrice
                )
            )
            Log.d("responseSetTargetPrice", response.toString())
            when (response) {
                is APIResult.Error -> {
                    _event.emit(SetTargetPriceEvent.FailureProductAdd)
                }

                is APIResult.Success -> {
                    _event.emit(SetTargetPriceEvent.SuccessProductAdd)
                }
            }
        }
    }

    fun patchProduct() {
        // TODO
    }

    fun updateTargetPrice(price: String) {
        if (price.toIntOrNull() != null) {
            _state.value = state.value.copy(targetPrice = price.toInt())
        }
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

package app.priceguard.ui.main.additem.setprice

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
        val price: Int = 0
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
        viewModelScope.launch {
            val response = productRepository.addProduct(
                ProductAddRequest(
                    state.value.productCode,
                    state.value.price
                )
            )
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
}

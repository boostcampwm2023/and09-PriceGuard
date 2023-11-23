package app.priceguard.ui.main.additem.link

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.ProductDTO
import app.priceguard.data.dto.ProductVerifyRequest
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
class RegisterItemLinkViewModel
@Inject constructor(private val productRepository: ProductRepository) : ViewModel() {

    data class RegisterLinkUIState(
        val link: String = "",
        val product: ProductDTO? = null,
        val isNextReady: Boolean = false,
        val isLinkError: Boolean? = null,
        val isVerificationFinished: Boolean = true
    )

    sealed class RegisterLinkEvent {
        data object LinkError : RegisterLinkEvent()
        data object SuccessVerification : RegisterLinkEvent()
    }

    private val _state = MutableStateFlow(RegisterLinkUIState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<RegisterLinkEvent>()
    val event = _event.asSharedFlow()

    fun verifyLink() {
        _state.value = state.value.copy(isVerificationFinished = false)

        viewModelScope.launch {
            val response = productRepository.verifyLink(ProductVerifyRequest(state.value.link))
            when (response) {
                is APIResult.Success -> {
                    _state.value = state.value.copy(
                        product = ProductDTO(
                            response.data.productName,
                            response.data.productCode,
                            response.data.price,
                            response.data.shop,
                            response.data.imageUrl
                        )
                    )
                }

                is APIResult.Error -> {
                    when (response.code) {
                        400 -> {
                            _state.value = state.value.copy(isLinkError = true)
                        }
                    }
                }
            }
            _state.value = state.value.copy(isVerificationFinished = true)
        }
    }

    fun updateLink(link: String) {
        _state.value = state.value.copy(link = link)
    }
}

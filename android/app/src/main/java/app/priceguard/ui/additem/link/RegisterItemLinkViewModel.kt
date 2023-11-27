package app.priceguard.ui.additem.link

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.ProductVerifyDTO
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
        val product: ProductVerifyDTO? = null,
        val isNextReady: Boolean = true,
        val isLinkError: Boolean = false
    )

    sealed class RegisterLinkEvent {
        data class SuccessVerification(val product: ProductVerifyDTO) : RegisterLinkEvent()
        data object FailureVerification : RegisterLinkEvent()
    }

    private val _state = MutableStateFlow(RegisterLinkUIState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<RegisterLinkEvent>()
    val event = _event.asSharedFlow()

    private fun isUrlValid(url: String): Boolean {
        val urlPattern =
            """(https?:\/\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\.[a-z]{2,6}\b([-a-zA-Z0-9@:%_\+.~#?&//=]*)""".toRegex()
        return urlPattern.matches(url)
    }

    fun verifyLink() {
        if (!isUrlValid(state.value.link)) {
            _state.value =
                state.value.copy(isLinkError = !isUrlValid(state.value.link), isNextReady = false)
            return
        }
        _state.value = state.value.copy(
            isNextReady = false,
            isLinkError = false
        )

        viewModelScope.launch {
            val response = productRepository.verifyLink(ProductVerifyRequest(state.value.link))
            when (response) {
                is APIResult.Success -> {
                    val product = ProductVerifyDTO(
                        response.data.productName,
                        response.data.productCode,
                        response.data.productPrice,
                        response.data.shop,
                        response.data.imageUrl
                    )
                    _state.value = state.value.copy(isNextReady = true, product = product)
                    _event.emit(RegisterLinkEvent.SuccessVerification(product))
                }

                is APIResult.Error -> {
                    when (response.code) {
                        400 -> {
                            _state.value = state.value.copy(isLinkError = true, isNextReady = false)
                        }
                    }
                    _event.emit(RegisterLinkEvent.FailureVerification)
                }
            }
        }
    }

    fun updateLink(link: String) {
        _state.value = state.value.copy(isLinkError = false, isNextReady = true, link = link)
    }
}

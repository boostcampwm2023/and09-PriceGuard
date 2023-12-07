package app.priceguard.ui.additem.link

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.product.ProductRepository
import app.priceguard.ui.data.ProductVerifyResult
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
        val product: ProductVerifyResult? = null,
        val isNextReady: Boolean = false,
        val isLinkError: Boolean = false
    )

    sealed class RegisterLinkEvent {
        data class SuccessVerification(val product: ProductVerifyResult) : RegisterLinkEvent()
        data class FailureVerification(val errorType: ProductErrorState) : RegisterLinkEvent()
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
            when (val response = productRepository.verifyLink(state.value.link)) {
                is RepositoryResult.Success -> {
                    _state.value = state.value.copy(isNextReady = true, product = response.data)
                    _event.emit(RegisterLinkEvent.SuccessVerification(response.data))
                }

                is RepositoryResult.Error -> {
                    _state.value = state.value.copy(isLinkError = true, isNextReady = false)
                    _event.emit(RegisterLinkEvent.FailureVerification(response.errorState))
                }
            }
        }
    }

    fun updateLink(link: String) {
        _state.value = state.value.copy(isLinkError = false, isNextReady = true, link = link)
    }
}

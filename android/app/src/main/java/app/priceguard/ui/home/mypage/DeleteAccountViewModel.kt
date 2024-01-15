package app.priceguard.ui.home.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.auth.AuthErrorState
import app.priceguard.data.repository.auth.AuthRepository
import app.priceguard.data.repository.token.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class DeleteAccountViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    data class DeleteAccountState(
        val email: String = "",
        val password: String = "",
        val isChecked: Boolean = false,
        val isDeleteEnabled: Boolean = false
    )

    sealed class DeleteAccountEvent {
        data object Logout : DeleteAccountEvent()
        data object WrongPassword : DeleteAccountEvent()
        data object UndefinedError : DeleteAccountEvent()
    }

    private val _state = MutableStateFlow(DeleteAccountState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<DeleteAccountEvent>()
    val event = _event.asSharedFlow()

    fun deleteAccount() {
        viewModelScope.launch {
            val response = authRepository.deleteAccount(_state.value.email, _state.value.password)
            when (response) {
                is RepositoryResult.Error -> {
                    when (response.errorState) {
                        AuthErrorState.INVALID_REQUEST -> {
                            _event.emit(DeleteAccountEvent.WrongPassword)
                        }

                        else -> {
                            _event.emit(DeleteAccountEvent.UndefinedError)
                        }
                    }
                }

                is RepositoryResult.Success -> {
                    tokenRepository.clearTokens()
                    _event.emit(DeleteAccountEvent.Logout)
                }
            }
        }
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)
    }

    fun updatePassWord(password: String) {
        _state.value = _state.value.copy(password = password)
        updateDeleteEnabled()
    }

    fun updateChecked(isChecked: Boolean) {
        _state.value = _state.value.copy(isChecked = isChecked)
        updateDeleteEnabled()
    }

    private fun updateDeleteEnabled() {
        _state.value = _state.value.copy(isDeleteEnabled = _state.value.password.isNotEmpty() && _state.value.isChecked)
    }
}

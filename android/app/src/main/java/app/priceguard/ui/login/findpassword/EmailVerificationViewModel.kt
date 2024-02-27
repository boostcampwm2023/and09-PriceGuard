package app.priceguard.ui.login.findpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.auth.AuthErrorState
import app.priceguard.data.repository.auth.AuthRepository
import app.priceguard.data.repository.token.TokenErrorState
import app.priceguard.data.repository.token.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    data class EmailVerificationState(
        val email: String = "",
        val verificationCode: String = "",
        val verifyToken: String = "",
        val isMatchedEmailRegex: Boolean = false,
        val isFinishedRequestVerificationCode: Boolean = false,
        val isNextEnabled: Boolean = false
    )

    sealed class EmailVerificationEvent {
        data object SuccessVerify : EmailVerificationEvent()
        data object SuccessRequestVerificationCode : EmailVerificationEvent()
        data object InvalidEmail : EmailVerificationEvent()
        data object WrongVerificationCode : EmailVerificationEvent()
        data object UndefinedError : EmailVerificationEvent()
    }

    private val _state = MutableStateFlow(EmailVerificationState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<EmailVerificationEvent>()
    val event = _event.asSharedFlow()

    fun requestVerificationCode() {
        viewModelScope.launch {
            when (val response = authRepository.requestVerificationCode(_state.value.email)) {
                is RepositoryResult.Error -> {
                    when (response.errorState) {
                        AuthErrorState.INVALID_REQUEST -> {
                            _event.emit(EmailVerificationEvent.InvalidEmail)
                        }

                        else -> _event.emit(EmailVerificationEvent.UndefinedError)
                    }
                }

                is RepositoryResult.Success -> {
                    _event.emit(EmailVerificationEvent.SuccessRequestVerificationCode)
                    _state.value = _state.value.copy(isFinishedRequestVerificationCode = true)
                    checkNextEnabled()
                }
            }
        }
    }

    fun verifyEmail() {
        viewModelScope.launch {
            when (
                val response =
                    tokenRepository.verifyEmail(_state.value.email, _state.value.verificationCode)
            ) {
                is RepositoryResult.Error -> {
                    when (response.errorState) {
                        TokenErrorState.UNAUTHORIZED -> {
                            _event.emit(EmailVerificationEvent.WrongVerificationCode)
                        }

                        else -> _event.emit(EmailVerificationEvent.UndefinedError)
                    }
                }

                is RepositoryResult.Success -> {
                    _state.value = _state.value.copy(verifyToken = response.data.verifyToken)
                    _event.emit(EmailVerificationEvent.SuccessVerify)
                }
            }
        }
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(
            email = email
        )
        checkEmailRegex(email)
    }

    fun updateVerificationCode(verificationCode: String) {
        _state.value = _state.value.copy(
            verificationCode = verificationCode
        )
        checkNextEnabled()
    }

    private fun checkEmailRegex(email: String) {
        val emailPattern =
            """^[\w.+-]+@((?!-)[A-Za-z0-9-]{1,63}(?<!-)\.)+[A-Za-z]{2,6}$""".toRegex()

        _state.value = _state.value.copy(
            isMatchedEmailRegex = email.matches(emailPattern)
        )
    }

    private fun checkNextEnabled() {
        _state.value = _state.value.copy(
            isNextEnabled = _state.value.verificationCode.length == 6 && _state.value.isFinishedRequestVerificationCode
        )
    }
}

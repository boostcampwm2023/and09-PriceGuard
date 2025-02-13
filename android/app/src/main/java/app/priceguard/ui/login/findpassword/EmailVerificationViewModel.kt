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

    enum class EmailVerificationType {
        REGISTER_VERIFICATION,
        VERIFICATION
    }

    data class EmailVerificationState(
        val email: String = "",
        val verificationCode: String = "",
        val verifyToken: String = "",
        val expirationTime: String = "",
        val isMatchedEmailRegex: Boolean = false,
        val isRequestedVerificationCode: Boolean = false,
        val isFinishedRequestVerificationCode: Boolean = false,
        val isNextEnabled: Boolean = false,
        val isFindPassword: Boolean = true
    )

    sealed class EmailVerificationEvent {
        data class SuccessVerify(val isFindPassword: Boolean) : EmailVerificationEvent()
        data object SuccessRequestVerificationCode : EmailVerificationEvent()
        data object NotFoundEmail : EmailVerificationEvent()
        data object ExpireToken : EmailVerificationEvent()
        data object WrongVerificationCode : EmailVerificationEvent()
        data object DuplicatedEmail : EmailVerificationEvent()
        data object OverVerificationLimit : EmailVerificationEvent()
        data object UndefinedError : EmailVerificationEvent()
    }

    private val _state = MutableStateFlow(EmailVerificationState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<EmailVerificationEvent>()
    val event = _event.asSharedFlow()

    fun requestVerificationCode(type: EmailVerificationType) {
        _state.value = _state.value.copy(isRequestedVerificationCode = true)

        viewModelScope.launch {
            val response = if (type == EmailVerificationType.VERIFICATION) {
                authRepository.requestVerificationCode(_state.value.email)
            } else {
                authRepository.requestRegisterVerificationCode(_state.value.email)
            }
            when (response) {
                is RepositoryResult.Error -> {
                    when (response.errorState) {
                        AuthErrorState.NOT_FOUND -> {
                            _event.emit(EmailVerificationEvent.NotFoundEmail)
                        }

                        AuthErrorState.DUPLICATED_EMAIL -> {
                            _event.emit(EmailVerificationEvent.DuplicatedEmail)
                        }

                        AuthErrorState.OVER_LIMIT -> {
                            _event.emit(EmailVerificationEvent.OverVerificationLimit)
                        }

                        else -> _event.emit(EmailVerificationEvent.UndefinedError)
                    }
                    _state.value = _state.value.copy(isRequestedVerificationCode = false)
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

                        TokenErrorState.NOT_FOUND -> {
                            _event.emit(EmailVerificationEvent.ExpireToken)
                            checkNextEnabled()
                        }

                        else -> _event.emit(EmailVerificationEvent.UndefinedError)
                    }
                }

                is RepositoryResult.Success -> {
                    tokenRepository.storeEmailVerified(true)
                    _state.value = _state.value.copy(verifyToken = response.data.verifyToken)
                    _event.emit(EmailVerificationEvent.SuccessVerify(_state.value.isFindPassword))
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

    fun updateTimer(timeString: String) {
        _state.value = _state.value.copy(expirationTime = timeString)
    }

    fun updateRetryVerificationCodeEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(
            isRequestedVerificationCode = !enabled
        )
    }

    fun updateType(isFindPassword: Boolean) {
        _state.value = _state.value.copy(
            isFindPassword = isFindPassword
        )
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

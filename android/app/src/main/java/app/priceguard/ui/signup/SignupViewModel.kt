package app.priceguard.ui.signup

import android.util.Log
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
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    data class SignupUIState(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val retypePassword: String = "",
        val isSignupReady: Boolean = false,
        val isNameError: Boolean? = null,
        val isEmailError: Boolean? = null,
        val isPasswordError: Boolean? = null,
        val isRetypePasswordError: Boolean? = null,
        val isSignupStarted: Boolean = false,
        val isSignupFinished: Boolean = false
    )

    sealed class SignupEvent {
        data object SignupStart : SignupEvent()
        data object InvalidRequest : SignupEvent()
        data object DuplicatedEmail : SignupEvent()
        data object UndefinedError : SignupEvent()
        data object SignupInfoSaved : SignupEvent()
    }

    private val emailPattern =
        """^[\w.+-]+@((?!-)[A-Za-z0-9-]{1,63}(?<!-)\.)+[A-Za-z]{2,6}$""".toRegex()
    private val passwordPattern =
        """^(?=[A-Za-z\d!@#$%^&*]*\d)(?=[A-Za-z\d!@#$%^&*]*[a-z])(?=[A-Za-z\d!@#$%^&*]*[A-Z])(?=[A-Za-z\d!@#$%^&*]*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,16}$""".toRegex()

    private val _state: MutableStateFlow<SignupUIState> = MutableStateFlow(SignupUIState())
    val state: StateFlow<SignupUIState> = _state.asStateFlow()

    private val _eventFlow: MutableSharedFlow<SignupEvent> = MutableSharedFlow(replay = 0)
    val eventFlow: SharedFlow<SignupEvent> = _eventFlow.asSharedFlow()

    fun signup() {
        if (_state.value.isSignupStarted || _state.value.isSignupFinished) {
            Log.d("Signup", "Signup already requested. Skipping")
            return
        }

        viewModelScope.launch {
            sendSignupEvent(SignupEvent.SignupStart)
            Log.d("ViewModel", "Event Start Sent")
            updateSignupStarted(true)
            val result =
                authRepository.signUp(_state.value.email, _state.value.name, _state.value.password)

            when (result) {
                is RepositoryResult.Success -> {
                    if (result.data.accessToken.isEmpty() || result.data.refreshToken.isEmpty()) {
                        sendSignupEvent(SignupEvent.UndefinedError)
                        updateSignupStarted(false)
                        return@launch
                    }

                    updateSignupFinished(true)
                    saveTokens(result.data.accessToken, result.data.refreshToken)
                    sendSignupEvent(SignupEvent.SignupInfoSaved)
                    Log.d("ViewModel", "Event Finish Sent")
                }

                is RepositoryResult.Error -> {
                    sendSignupEvent(
                        when (result.errorState) {
                            AuthErrorState.INVALID_REQUEST -> {
                                SignupEvent.InvalidRequest
                            }

                            AuthErrorState.DUPLICATED_EMAIL -> {
                                SignupEvent.DuplicatedEmail
                            }

                            AuthErrorState.UNDEFINED_ERROR -> {
                                SignupEvent.UndefinedError
                            }
                        }
                    )
                }
            }
            updateSignupStarted(false)
        }
    }

    fun updateName(name: String) {
        _state.value = _state.value.copy(name = name)

        updateNameError()
        updateIsSignupReady()
    }

    fun updateEmail(email: String) {
        _state.value = _state.value.copy(email = email)

        updateEmailError()
        updateIsSignupReady()
    }

    fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)

        updatePasswordError()
        updateRetypePasswordError()
        updateIsSignupReady()
    }

    fun updateRetypePassword(retypePassword: String) {
        _state.value = _state.value.copy(retypePassword = retypePassword)

        updateRetypePasswordError()
        updateIsSignupReady()
    }

    private fun isValidName(): Boolean {
        return _state.value.name.isNotBlank()
    }

    private fun isValidEmail(): Boolean {
        return emailPattern.matchEntire(_state.value.email) != null
    }

    private fun isValidPassword(): Boolean {
        return passwordPattern.matchEntire(_state.value.password) != null
    }

    private fun isValidRetypePassword(): Boolean {
        return _state.value.retypePassword.isNotBlank() && _state.value.password == _state.value.retypePassword
    }

    private suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenRepository.storeTokens(accessToken, refreshToken)
    }

    private suspend fun sendSignupEvent(event: SignupEvent) {
        _eventFlow.emit(event)
    }

    private fun updateIsSignupReady() {
        _state.value =
            _state.value.copy(isSignupReady = isValidName() && isValidEmail() && isValidPassword() && isValidRetypePassword())
    }

    private fun updateNameError() {
        _state.value.let { uiState ->
            if (isValidName()) {
                _state.value = uiState.copy(isNameError = false)
            } else {
                _state.value = uiState.copy(isNameError = true)
            }
        }
    }

    private fun updateEmailError() {
        _state.value.let { uiState ->
            when {
                isValidEmail() -> {
                    _state.value = uiState.copy(isEmailError = false)
                }

                uiState.email.isEmpty() -> {
                    _state.value = uiState.copy(isEmailError = null)
                }

                else -> {
                    _state.value = uiState.copy(isEmailError = true)
                }
            }
        }
    }

    private fun updatePasswordError() {
        _state.value.let { uiState ->
            when {
                isValidPassword() -> {
                    _state.value = uiState.copy(isPasswordError = false)
                }

                uiState.password.isEmpty() -> {
                    _state.value = uiState.copy(isPasswordError = null)
                }

                else -> {
                    _state.value = uiState.copy(isPasswordError = true)
                }
            }
        }
    }

    private fun updateRetypePasswordError() {
        _state.value.let { uiState ->
            when {
                isValidRetypePassword() -> {
                    _state.value = uiState.copy(isRetypePasswordError = false)
                }

                uiState.retypePassword.isEmpty() -> {
                    _state.value = uiState.copy(isRetypePasswordError = null)
                }

                else -> {
                    _state.value = uiState.copy(isRetypePasswordError = true)
                }
            }
        }
    }

    private fun updateSignupStarted(started: Boolean) {
        _state.value = _state.value.copy(isSignupStarted = started)
    }

    private fun updateSignupFinished(finished: Boolean) {
        _state.value = _state.value.copy(isSignupFinished = finished)
    }
}

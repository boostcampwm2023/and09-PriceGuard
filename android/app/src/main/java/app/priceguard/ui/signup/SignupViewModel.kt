package app.priceguard.ui.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignupViewModel : ViewModel() {

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
        val isSignupStarted: Boolean = false
    )

    private val emailPattern =
        """^[\w.+-]+@((?!-)[A-Za-z0-9-]{1,63}(?<!-)\.)+[A-Za-z]{2,6}$""".toRegex()
    private val passwordPattern =
        """^(?=[A-Za-z\d!@#$%^&*]*\d)(?=[A-Za-z\d!@#$%^&*]*[a-z])(?=[A-Za-z\d!@#$%^&*]*[A-Z])(?=[A-Za-z\d!@#$%^&*]*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,16}$""".toRegex()

    private val _state: MutableStateFlow<SignupUIState> = MutableStateFlow(SignupUIState())
    val state: StateFlow<SignupUIState> = _state.asStateFlow()

    private val _eventFlow: MutableSharedFlow<SignupEvent> = MutableSharedFlow(replay = 0)
    val eventFlow: SharedFlow<SignupEvent> = _eventFlow.asSharedFlow()

    fun signup() {
        viewModelScope.launch {
            if (_state.value.isSignupStarted) {
                Log.d("Signup", "Signup already requested. Skipping")
                return@launch
            }

            sendEvent(SignupEvent.SignupStart)
            updateSignupStarted(true)
            Log.d("ViewModel", "Event Start Sent")
            delay(3000L)
            // TODO: 제거하고 Signup 네트워크 로직 넣기
            // TODO: 회원가입 성공시 로그인 정보 저장하기
            // TODO: 회원가입 실패시 메세지 표시하기
            sendEvent(SignupEvent.SignupFinish)
            updateSignupStarted(false)
            Log.d("ViewModel", "Event Finish Sent")
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

    private fun sendEvent(event: SignupEvent) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
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

    sealed class SignupEvent {
        object SignupStart : SignupEvent()
        object SignupFinish : SignupEvent()
        object SignupError : SignupEvent()
    }
}
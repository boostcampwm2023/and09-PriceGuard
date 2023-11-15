package app.priceguard.ui.signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignupViewModel : ViewModel() {
    data class SignupUIState(
        val name: String = "",
        val email: String = "",
        val password: String = "",
        val retypePassword: String = "",
        val isSignupReady: Boolean = false,
        val isSignupComplete: Boolean = false,
        val isNameError: Boolean? = null,
        val isEmailError: Boolean? = null,
        val isPasswordError: Boolean? = null,
        val isRetypePasswordError: Boolean? = null
    )

    private val emailPattern = """^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\.[a-zA-Z0-9-.]+$""".toRegex()
    private val passwordPattern = """^[a-zA-Z0-9!@#$%^&*]{8,16}$""".toRegex()

    private val _state: MutableStateFlow<SignupUIState> = MutableStateFlow(SignupUIState())
    val state: StateFlow<SignupUIState> = _state.asStateFlow()

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
        updateIsSignupReady()
    }

    fun updateRetypePassword(retypePassword: String) {
        _state.value = _state.value.copy(retypePassword = retypePassword)

        updateRetypePasswordError()
        updateIsSignupReady()
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
}

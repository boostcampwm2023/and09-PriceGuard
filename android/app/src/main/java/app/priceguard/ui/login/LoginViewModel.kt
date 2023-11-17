package app.priceguard.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.LoginResult
import app.priceguard.data.dto.LoginState
import app.priceguard.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    data class State(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false
    )

    sealed interface LoginEvent {
        data object StartLoading : LoginEvent
        data object Invalid : LoginEvent
        data class LoginFailed(val status: LoginState) : LoginEvent
        data class LoginSuccess(val accessToken: String, val refreshToken: String) : LoginEvent
    }

    private val emailPattern = """^[\w.+-]+@((?!-)[A-Za-z0-9-]{1,63}(?<!-)\.)+[A-Za-z]{2,6}$""".toRegex()
    private val passwordPattern = """^(?=[A-Za-z\d!@#$%^&*]*\d)(?=[A-Za-z\d!@#$%^&*]*[a-z])(?=[A-Za-z\d!@#$%^&*]*[A-Z])(?=[A-Za-z\d!@#$%^&*]*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,16}$""".toRegex()

    lateinit var userRepository: UserRepository

    private var _event = MutableSharedFlow<LoginEvent>()
    val event: SharedFlow<LoginEvent> = _event.asSharedFlow()
    private val _state = MutableStateFlow(State())
    var state: StateFlow<State> = _state.asStateFlow()

    fun setID(s: CharSequence, start: Int, before: Int, count: Int) {
        if (_state.value.isLoading) return
        _state.value = _state.value.copy(email = s.toString())
    }

    fun setPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        if (_state.value.isLoading) return
        _state.value = _state.value.copy(password = s.toString())
    }

    fun login() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            _event.emit(LoginEvent.StartLoading)
            if (checkEmailAndPassword()) {
                val result = userRepository.login(_state.value.email, _state.value.password)
                sendEvent(result)
            } else {
                _event.emit(LoginEvent.Invalid)
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun sendEvent(result: LoginResult) {
        when (result.loginState) {
            LoginState.SUCCESS -> {
                if (result.loginResponse == null) {
                    _event.emit(LoginEvent.LoginFailed(LoginState.UNDEFINED_ERROR))
                } else {
                    _event.emit(LoginEvent.LoginSuccess(result.loginResponse.accessToken, result.loginResponse.refreshToken))
                }
            }

            else -> {
                _event.emit(LoginEvent.LoginFailed(result.loginState))
            }
        }
    }

    private fun checkEmailAndPassword(): Boolean {
        return emailPattern.matchEntire(_state.value.email) != null && passwordPattern.matchEntire(_state.value.password) != null
    }
}

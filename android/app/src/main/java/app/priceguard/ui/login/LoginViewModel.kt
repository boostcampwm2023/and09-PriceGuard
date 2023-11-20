package app.priceguard.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.LoginResponse
import app.priceguard.data.dto.LoginState
import app.priceguard.data.repository.UserRepository
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
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    data class State(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val isLoginFinished: Boolean = false
    )

    sealed interface LoginEvent {
        data object LoginStart : LoginEvent
        data object Invalid : LoginEvent
        data class LoginSuccess(val response: LoginResponse?) : LoginEvent
        data class LoginFailure(val status: LoginState) : LoginEvent
    }

    private val emailPattern =
        """^[\w.+-]+@((?!-)[A-Za-z0-9-]{1,63}(?<!-)\.)+[A-Za-z]{2,6}$""".toRegex()
    private val passwordPattern =
        """^(?=[A-Za-z\d!@#$%^&*]*\d)(?=[A-Za-z\d!@#$%^&*]*[a-z])(?=[A-Za-z\d!@#$%^&*]*[A-Z])(?=[A-Za-z\d!@#$%^&*]*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,16}$""".toRegex()

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
        if (_state.value.isLoading || _state.value.isLoginFinished) {
            Log.d("Login", "Login already requested. Skipping")
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            sendLoginEvent(LoginEvent.LoginStart)

            if (checkEmailAndPassword()) {
                val result = userRepository.login(_state.value.email, _state.value.password)

                when (result.loginState) {
                    LoginState.SUCCESS -> {
                        _state.value = _state.value.copy(isLoginFinished = true)
                        sendLoginEvent(
                            LoginEvent.LoginSuccess(result.loginResponse)
                        )
                    }

                    else -> {
                        sendLoginEvent(LoginEvent.LoginFailure(result.loginState))
                    }
                }
            } else {
                sendLoginEvent(LoginEvent.Invalid)
            }
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    private suspend fun sendLoginEvent(event: LoginEvent) {
        _event.emit(event)
    }

    private fun checkEmailAndPassword(): Boolean {
        return emailPattern.matchEntire(_state.value.email) != null && passwordPattern.matchEntire(
            _state.value.password
        ) != null
    }
}

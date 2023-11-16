package app.priceguard.ui.login

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

class LoginViewModel : ViewModel() {

    data class State(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false
    )

    sealed interface LoginEvent {
        object StartLoading : LoginEvent
        object Invalid : LoginEvent
        object LoginFailed : LoginEvent
        object LoginSuccess : LoginEvent
    }

    private val emailPattern = """^[\w.+-]+@((?!-)[A-Za-z0-9-]{1,63}(?<!-)\.)+[A-Za-z]{2,6}$""".toRegex()
    private val passwordPattern = """^(?=[A-Za-z\d!@#$%^&*]*\d)(?=[A-Za-z\d!@#$%^&*]*[a-z])(?=[A-Za-z\d!@#$%^&*]*[A-Z])(?=[A-Za-z\d!@#$%^&*]*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,16}$""".toRegex()

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
        if (checkEmailAndPassword()) {
            // TODO: 서버에 정보 전송
        } else {
            viewModelScope.launch {
                _event.emit(LoginEvent.StartLoading)
                delay(2000)
                _event.emit(LoginEvent.Invalid)
            }
        }
        _state.value = _state.value.copy(isLoading = false)
    }

    private fun checkEmailAndPassword(): Boolean {
        return emailPattern.matchEntire(_state.value.email) != null && passwordPattern.matchEntire(_state.value.password) != null
    }
}

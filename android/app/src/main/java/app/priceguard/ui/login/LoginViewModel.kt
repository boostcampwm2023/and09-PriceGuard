package app.priceguard.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    data class State(
        val id: String = "",
        val password: String = ""
    )

    sealed interface LoginEvent {
        object GoToSignUp : LoginEvent
        object LoginFailed : LoginEvent
    }

    private var _event = MutableSharedFlow<LoginEvent>()
    val event: SharedFlow<LoginEvent> = _event.asSharedFlow()

    private val _state = MutableStateFlow(State())
    var state: StateFlow<State> = _state.asStateFlow()

    fun setID(s: CharSequence, start: Int, before: Int, count: Int) {
        _state.value = _state.value.copy(id = s.toString())
    }

    fun setPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        _state.value = _state.value.copy(password = s.toString())
    }

    fun logIn() {
        viewModelScope.launch {
            _event.emit(LoginEvent.LoginFailed)
        }
    }

    fun signUp() {
        viewModelScope.launch {
            _event.emit(LoginEvent.GoToSignUp)
        }
    }
}

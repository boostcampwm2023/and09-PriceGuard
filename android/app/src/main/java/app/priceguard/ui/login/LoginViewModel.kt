package app.priceguard.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    data class State(
        val email: String = "",
        val password: String = ""
    )

    private val _state = MutableStateFlow(State())
    var state: StateFlow<State> = _state.asStateFlow()

    fun setID(s: CharSequence, start: Int, before: Int, count: Int) {
        _state.value = _state.value.copy(email = s.toString())
    }

    fun setPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        _state.value = _state.value.copy(password = s.toString())
    }

    fun logIn(onSuccess: (Unit) -> Unit) {
        val emailPattern = Regex("""^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}${'$'}""")
        val passwordPattern = Regex("""(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#${'$'}%^&*]).{8,16}""")
        if (emailPattern.matches(_state.value.email) && passwordPattern.matches(_state.value.password)) {
            // TODO: 서버에 정보 전송
        } else {
            onSuccess.invoke(Unit)
        }
    }
}

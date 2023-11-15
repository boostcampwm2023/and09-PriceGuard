package app.priceguard.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    data class State(
        val id: String = "",
        val password: String = ""
    )

    private val _state = MutableStateFlow(State())
    var state: StateFlow<State> = _state.asStateFlow()

    fun setID(s: CharSequence, start: Int, before: Int, count: Int) {
        _state.value = _state.value.copy(id = s.toString())
    }

    fun setPassword(s: CharSequence, start: Int, before: Int, count: Int) {
        _state.value = _state.value.copy(password = s.toString())
    }

    fun logIn(onSuccess: (Unit) -> Unit) {
        val emailPattern = Regex("""^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}${'$'}""")
        val passwordPattern = Regex("""(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#${'$'}%^&*]).{8,16}""")
        if (emailPattern.matches(_state.value.id) && passwordPattern.matches(_state.value.password)) {
            // TODO: 서버에 정보 전송
        } else {
            onSuccess.invoke(Unit)
        }
    }
}

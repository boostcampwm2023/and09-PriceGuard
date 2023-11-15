package app.priceguard.ui.login

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val emailPattern = """^[\w.+-]+@((?!-)[A-Za-z0-9-]{1,63}(?<!-)\.)+[A-Za-z]{2,6}$""".toRegex()
    private val passwordPattern = """^(?=[A-Za-z\d!@#$%^&*]*\d)(?=[A-Za-z\d!@#$%^&*]*[a-z])(?=[A-Za-z\d!@#$%^&*]*[A-Z])(?=[A-Za-z\d!@#$%^&*]*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{8,16}$""".toRegex()

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
        if (checkEmailAndPassword()) {
            // TODO: 서버에 정보 전송
        } else {
            onSuccess.invoke(Unit)
        }
    }

    private fun checkEmailAndPassword(): Boolean {
        return emailPattern.matches(_state.value.email) && passwordPattern.matches(_state.value.password)
    }
}

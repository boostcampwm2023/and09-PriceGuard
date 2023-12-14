package app.priceguard.ui.login

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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {

    data class State(
        val email: String = "",
        val password: String = "",
        val isLoading: Boolean = false,
        val isLoginFinished: Boolean = false
    )

    sealed class LoginEvent {
        data object LoginStart : LoginEvent()
        data object Invalid : LoginEvent()
        data object LoginFailure : LoginEvent()
        data object UndefinedError : LoginEvent()
        data object LoginInfoSaved : LoginEvent()
        data object FirebaseError : LoginEvent()
        data object TokenUpdateError : LoginEvent()
    }

    private val emailPattern =
        """^[\w.+-]+@((?!-)[A-Za-z0-9-]{1,63}(?<!-)\.)+[A-Za-z]{2,6}$""".toRegex()
    private val passwordPattern =
        """^(?=[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]*\d)(?=[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]*[a-z])(?=[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]*[A-Z])(?=[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]*[!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/])[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]{8,16}$""".toRegex()

    private var _event = MutableSharedFlow<LoginEvent>()
    val event: SharedFlow<LoginEvent> = _event.asSharedFlow()
    private val _state = MutableStateFlow(State())
    var state: StateFlow<State> = _state.asStateFlow()

    fun setEmail(s: String) {
        if (_state.value.isLoading) return
        _state.value = _state.value.copy(email = s)
    }

    fun setPassword(s: String) {
        if (_state.value.isLoading) return
        _state.value = _state.value.copy(password = s)
    }

    fun login() {
        if (_state.value.isLoading || _state.value.isLoginFinished) {
            Log.d("Login", "Login already requested. Skipping")
            return
        }

        viewModelScope.launch {
            setLoading(true)
            sendLoginEvent(LoginEvent.LoginStart)

            if (!checkEmailAndPassword()) {
                sendLoginEvent(LoginEvent.Invalid)
                setLoading(false)
                return@launch
            }

            when (val result = authRepository.login(_state.value.email, _state.value.password)) {
                is RepositoryResult.Success -> {
                    if (result.data.accessToken.isEmpty() || result.data.refreshToken.isEmpty()) {
                        sendLoginEvent(LoginEvent.UndefinedError)
                        setLoading(false)
                        return@launch
                    }

                    val firebaseToken = tokenRepository.getFirebaseToken()
                    setLoginFinished(true)
                    saveTokens(result.data.accessToken, result.data.refreshToken)
                    updateFirebaseToken(result.data.accessToken, firebaseToken)
                    sendLoginEvent(LoginEvent.LoginInfoSaved)
                }

                is RepositoryResult.Error -> {
                    sendLoginEvent(
                        when (result.errorState) {
                            AuthErrorState.INVALID_REQUEST -> {
                                LoginEvent.LoginFailure
                            }

                            else -> {
                                LoginEvent.UndefinedError
                            }
                        }
                    )
                }
            }
            setLoading(false)
        }
    }

    private suspend fun updateFirebaseToken(accessToken: String, firebaseToken: String?) {
        if (firebaseToken != null) {
            when (tokenRepository.updateFirebaseToken(accessToken, firebaseToken)) {
                is RepositoryResult.Error -> {
                    sendLoginEvent(LoginEvent.TokenUpdateError)
                }
                else -> {}
            }
        } else {
            sendLoginEvent(LoginEvent.FirebaseError)
        }
    }

    private suspend fun saveTokens(accessToken: String, refreshToken: String) {
        tokenRepository.storeTokens(accessToken, refreshToken)
    }

    private suspend fun sendLoginEvent(event: LoginEvent) {
        _event.emit(event)
    }

    private fun setLoading(isLoading: Boolean) {
        _state.value = _state.value.copy(isLoading = isLoading)
    }

    private fun setLoginFinished(finished: Boolean) {
        _state.value = _state.value.copy(isLoginFinished = finished)
    }

    private fun checkEmailAndPassword(): Boolean {
        return emailPattern.matchEntire(_state.value.email) != null && passwordPattern.matchEntire(
            _state.value.password
        ) != null
    }
}

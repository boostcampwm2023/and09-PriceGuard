package app.priceguard.ui.home.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.token.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val tokenRepository: TokenRepository
) : ViewModel() {

    sealed class MyPageEvent {
        data object StartIntroAndExitHome : MyPageEvent()
        data object StartVerifyEmail : MyPageEvent()
    }

    data class MyPageInfo(
        val name: String,
        val email: String,
        val firstName: String,
        val isEmailVerified: Boolean? = null
    )

    private val _state = MutableStateFlow(MyPageInfo("", "", ""))
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<MyPageEvent>()
    val event = _event.asSharedFlow()

    init {
        setInfo()
    }

    fun logout() {
        viewModelScope.launch {
            tokenRepository.clearTokens()
            _event.emit(MyPageEvent.StartIntroAndExitHome)
        }
    }

    fun getIsEmailVerified() {
        viewModelScope.launch {
            val isEmailVerified = tokenRepository.getIsEmailVerified()
            _state.value = _state.value.copy(isEmailVerified = isEmailVerified)
        }
    }

    fun startVerifyEmail() {
        viewModelScope.launch {
            _event.emit(MyPageEvent.StartVerifyEmail)
        }
    }

    private fun setInfo() {
        viewModelScope.launch {
            val userData = tokenRepository.getUserData()
            _state.value =
                MyPageInfo(
                    userData.name,
                    userData.email,
                    getFirstName(userData.name)
                )
        }
    }

    private fun getFirstName(name: String): String {
        name.forEach {
            if (it.isSurrogate().not() && it.toString().isNotBlank()) {
                return it.uppercase()
            }
        }
        return ""
    }
}

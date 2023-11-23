package app.priceguard.ui.home.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class MyPageViewModel @Inject constructor(
    val tokenRepository: TokenRepository
) : ViewModel() {

    sealed class MyPageEvent {
        data object StartIntroAndExitHome : MyPageEvent()
    }

    data class MyPageInfo(
        val name: String,
        val email: String,
        val firstName: String
    )

    private val _flow = MutableStateFlow(MyPageInfo("", "", ""))
    val flow = _flow.asStateFlow()

    private val _event = MutableSharedFlow<MyPageEvent>()
    val event = _event.asSharedFlow()

    init {
        setInfo()
    }

    private fun setInfo() {
        viewModelScope.launch {
            val userData = tokenRepository.getUserData()
            _flow.value =
                MyPageInfo(userData.name, userData.email, if (userData.name.isNotEmpty()) userData.name.first().toString() else "")
        }
    }

    fun logout() {
        viewModelScope.launch {
            val resetTokenJob = launch {
                tokenRepository.clearTokens()
            }
            resetTokenJob.join()
            if (resetTokenJob.isCompleted) {
                _event.emit(MyPageEvent.StartIntroAndExitHome)
            }
        }
    }
}

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
class MyPageViewModel @Inject constructor(val tokenRepository: TokenRepository) : ViewModel() {

    // 더미 데이터
    data class MyPageInfo(
        val name: String = "박승준 님",
        val email: String = "aaa@aa.aa",
        val firstName: String = name.first().toString()
    )

    private val _flow = MutableStateFlow(MyPageInfo())
    val flow = _flow.asStateFlow()

    private val _event = MutableSharedFlow<MyPageEvent>()
    val event = _event.asSharedFlow()

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

    sealed class MyPageEvent {
        data object StartIntroAndExitHome : MyPageEvent()
    }
}

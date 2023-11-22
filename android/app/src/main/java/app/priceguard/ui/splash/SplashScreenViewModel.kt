package app.priceguard.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.RenewResult
import app.priceguard.data.repository.TokenRepository
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
class SplashScreenViewModel @Inject constructor(tokenRepository: TokenRepository) : ViewModel() {

    sealed class SplashEvent {
        data object OpenIntro : SplashEvent()
        data object OpenHome : SplashEvent()
    }

    init {
        viewModelScope.launch {
            val accessToken = tokenRepository.getAccessToken()
            val refreshToken = tokenRepository.getRefreshToken()


            if (accessToken == null || refreshToken == null) {
                // Send Intro Event
                tokenRepository.clearTokens()
                sendSplashEvent(SplashEvent.OpenIntro)
                setAsReady()
                return@launch
            }

            // Renew Token
            val renewResult = tokenRepository.renewTokens(refreshToken)
            if (renewResult == RenewResult.SUCCESS) {
                sendSplashEvent(SplashEvent.OpenHome)
                setAsReady()
                return@launch
            }

            // Renew Failed
            tokenRepository.clearTokens()
            sendSplashEvent(SplashEvent.OpenIntro)
            setAsReady()
        }
    }

    private val _isReady: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

    private val _event: MutableSharedFlow<SplashEvent> = MutableSharedFlow()
    val event: SharedFlow<SplashEvent> = _event.asSharedFlow()

    private suspend fun sendSplashEvent(event: SplashEvent) {
        _event.emit(event)
    }

    private fun setAsReady() {
        _isReady.value = true
    }
}

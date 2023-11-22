package app.priceguard.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    data class Tokens(
        val accessToken: String? = null,
        val refreshToken: String? = null
    )

    sealed class SplashEvent {
        data object OpenIntro : SplashEvent()
        data object OpenHome : SplashEvent()
    }

    init {
        viewModelScope.launch {
            val accessToken = tokenRepository.getAccessToken()
            val refreshToken = tokenRepository.getRefreshToken()

            updateTokens(accessToken, refreshToken)

            if (accessToken == null || refreshToken == null) {
                // Send Intro Event
                tokenRepository.clearTokens()
                sendSplashEvent(SplashEvent.OpenIntro)
                setAsReady()
                return@launch
            }

            // TODO: Revalidate Token
            sendSplashEvent(SplashEvent.OpenHome)
            setAsReady()
        }
    }

    private val _tokens: MutableStateFlow<Tokens> = MutableStateFlow(Tokens())
    val tokens: StateFlow<Tokens> = _tokens.asStateFlow()

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

    private fun updateTokens(accessToken: String?, refreshToken: String?) {
        _tokens.value = _tokens.value.copy(accessToken = accessToken, refreshToken = refreshToken)
    }
}

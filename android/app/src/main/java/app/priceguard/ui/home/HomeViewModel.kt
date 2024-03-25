package app.priceguard.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.token.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    val tokenRepository: TokenRepository
) : ViewModel() {

    fun updateIsEmailVerified() {
        viewModelScope.launch {
            val response = tokenRepository.updateIsEmailVerified()

            if (response is RepositoryResult.Success) {
                saveEmailVerified(response.data)
            }
        }
    }

    private suspend fun saveEmailVerified(isVerified: Boolean) {
        tokenRepository.storeEmailVerified(isVerified)
    }
}

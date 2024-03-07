package app.priceguard.data.repository.token

import app.priceguard.data.repository.RepositoryResult
import app.priceguard.ui.data.UserDataResult

interface TokenRepository {
    suspend fun storeTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getFirebaseToken(): String?
    suspend fun updateFirebaseToken(accessToken: String, firebaseToken: String): RepositoryResult<Boolean, TokenErrorState>
    suspend fun getUserData(): UserDataResult
    suspend fun renewTokens(refreshToken: String): RepositoryResult<Boolean, TokenErrorState>
    suspend fun clearTokens()
    suspend fun updateIsEmailVerified(): RepositoryResult<Boolean, TokenErrorState>
    suspend fun storeEmailVerified(isVerified: Boolean)
    suspend fun getIsEmailVerified(): Boolean?
}

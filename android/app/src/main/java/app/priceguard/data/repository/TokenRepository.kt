package app.priceguard.data.repository

import app.priceguard.ui.data.UserDataResult

interface TokenRepository {
    suspend fun storeTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getUserData(): UserDataResult
    suspend fun renewTokens(refreshToken: String): RepositoryResult<Boolean, TokenErrorState>
    suspend fun clearTokens()
}

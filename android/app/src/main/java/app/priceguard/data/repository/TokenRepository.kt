package app.priceguard.data.repository

import app.priceguard.data.dto.RenewResult

interface TokenRepository {
    suspend fun storeTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun renewTokens(refreshToken: String): RenewResult
    suspend fun clearTokens()
}

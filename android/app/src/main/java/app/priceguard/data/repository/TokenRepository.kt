package app.priceguard.data.repository

interface TokenRepository {
    suspend fun storeTokens(accessToken: String, refreshToken: String)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun clearTokens()
}

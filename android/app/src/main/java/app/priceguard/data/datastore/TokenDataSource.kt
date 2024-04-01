package app.priceguard.data.datastore

interface TokenDataSource {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun saveEmailVerified(isVerified: Boolean)
    suspend fun getAccessToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun getIsEmailVerified(): Boolean?
    suspend fun clearTokens()
}

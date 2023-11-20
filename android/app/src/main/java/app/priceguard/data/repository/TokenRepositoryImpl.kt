package app.priceguard.data.repository

import app.priceguard.data.datastore.TokenDataSource
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(private val tokenDataSource: TokenDataSource) : TokenRepository {

    override suspend fun storeTokens(accessToken: String, refreshToken: String) {
        tokenDataSource.saveTokens(accessToken, refreshToken)
    }

    override suspend fun getAccessToken(): String? {
        return tokenDataSource.getAccessToken()
    }

    override suspend fun getRefreshToken(): String? {
        return tokenDataSource.getRefreshToken()
    }

    override suspend fun clearTokens() {
        tokenDataSource.clearTokens()
    }
}

package app.priceguard.data.repository

import app.priceguard.data.datastore.TokenDataSource
import app.priceguard.data.dto.RenewResult
import app.priceguard.data.network.APIResult
import app.priceguard.data.network.AuthAPI
import app.priceguard.data.network.getApiResult
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenDataSource: TokenDataSource,
    private val authAPI: AuthAPI
) : TokenRepository {

    override suspend fun storeTokens(accessToken: String, refreshToken: String) {
        tokenDataSource.saveTokens(accessToken, refreshToken)
    }

    override suspend fun getAccessToken(): String? {
        return tokenDataSource.getAccessToken()
    }

    override suspend fun getRefreshToken(): String? {
        return tokenDataSource.getRefreshToken()
    }

    override suspend fun renewTokens(refreshToken: String): RenewResult {
        when (val response = getApiResult { authAPI.renewTokens("Bearer $refreshToken") }) {
            is APIResult.Success -> {
                storeTokens(response.data.accessToken, response.data.refreshToken)
                return RenewResult.SUCCESS
            }

            is APIResult.Error -> {
                return when (response.code) {
                    401 -> {
                        RenewResult.UNAUTHORIZED
                    }

                    410 -> {
                        RenewResult.EXPIRED
                    }

                    else -> {
                        RenewResult.UNKNOWN_ERROR
                    }
                }
            }
        }
    }

    override suspend fun clearTokens() {
        tokenDataSource.clearTokens()
    }
}

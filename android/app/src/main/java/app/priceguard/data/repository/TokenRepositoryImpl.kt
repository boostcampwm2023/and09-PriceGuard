package app.priceguard.data.repository

import android.util.Log
import app.priceguard.data.datastore.TokenDataSource
import app.priceguard.data.network.AuthAPI
import app.priceguard.ui.data.RenewState
import app.priceguard.ui.data.UserDataResult
import java.util.*
import javax.inject.Inject
import kotlinx.serialization.json.Json

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

    override suspend fun getUserData(): UserDataResult {
        val accessToken = tokenDataSource.getAccessToken() ?: return UserDataResult("", "")
        val parts = accessToken.split(".")
        return try {
            val charset = charset("UTF-8")
            val payload = Json.decodeFromString<TokenUserData>(
                String(Base64.getUrlDecoder().decode(parts[1].toByteArray(charset)), charset)
            )
            UserDataResult(payload.email, payload.name)
        } catch (e: Exception) {
            Log.e("Data Not Found", "Error parsing JWT: $e")
            UserDataResult("", "")
        }
    }

    override suspend fun renewTokens(refreshToken: String): RenewState {
        when (val response = getApiResult { authAPI.renewTokens("Bearer $refreshToken") }) {
            is APIResult.Success -> {
                storeTokens(response.data.accessToken, response.data.refreshToken)
                return RenewState.SUCCESS
            }

            is APIResult.Error -> {
                return when (response.code) {
                    401 -> {
                        RenewState.UNAUTHORIZED
                    }

                    410 -> {
                        RenewState.EXPIRED
                    }

                    else -> {
                        RenewState.UNKNOWN_ERROR
                    }
                }
            }
        }
    }

    override suspend fun clearTokens() {
        tokenDataSource.clearTokens()
    }
}

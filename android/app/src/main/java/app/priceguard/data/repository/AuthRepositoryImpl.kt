package app.priceguard.data.repository

import app.priceguard.data.dto.login.LoginRequest
import app.priceguard.data.dto.signup.SignupRequest
import app.priceguard.data.network.APIResult
import app.priceguard.data.network.AuthErrorState
import app.priceguard.data.network.AuthRepositoryResult
import app.priceguard.data.network.UserAPI
import app.priceguard.data.network.getApiResult
import app.priceguard.ui.data.LoginResult
import app.priceguard.ui.data.SignupResult
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val userAPI: UserAPI) : AuthRepository {

    private fun <T> handleError(
        code: Int?
    ): AuthRepositoryResult<T> {
        return when (code) {
            400 -> {
                AuthRepositoryResult.Error(AuthErrorState.INVALID_REQUEST)
            }

            409 -> {
                AuthRepositoryResult.Error(AuthErrorState.DUPLICATED_EMAIL)
            }

            else -> {
                AuthRepositoryResult.Error(AuthErrorState.UNDEFINED_ERROR)
            }
        }
    }

    override suspend fun signUp(
        email: String,
        userName: String,
        password: String
    ): AuthRepositoryResult<SignupResult> {
        val response = getApiResult {
            userAPI.register(SignupRequest(email, userName, password))
        }
        return when (response) {
            is APIResult.Success -> {
                AuthRepositoryResult.Success(
                    SignupResult(
                        response.data.accessToken ?: "",
                        response.data.refreshToken ?: ""
                    )
                )
            }

            is APIResult.Error -> {
                handleError(response.code)
            }
        }
    }

    override suspend fun login(email: String, password: String): AuthRepositoryResult<LoginResult> {
        val response = getApiResult {
            userAPI.login(LoginRequest(email, password))
        }
        return when (response) {
            is APIResult.Success -> {
                AuthRepositoryResult.Success(
                    LoginResult(
                        response.data.accessToken ?: "",
                        response.data.refreshToken ?: ""
                    )
                )
            }

            is APIResult.Error -> {
                handleError(response.code)
            }
        }
    }
}

package app.priceguard.data.repository.auth

import app.priceguard.data.dto.login.LoginRequest
import app.priceguard.data.dto.signup.SignupRequest
import app.priceguard.data.network.UserAPI
import app.priceguard.data.repository.APIResult
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.getApiResult
import app.priceguard.ui.data.LoginResult
import app.priceguard.ui.data.SignupResult
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(private val userAPI: UserAPI) : AuthRepository {

    private fun <T> handleError(
        code: Int?
    ): RepositoryResult<T, AuthErrorState> {
        return when (code) {
            400 -> {
                RepositoryResult.Error(AuthErrorState.INVALID_REQUEST)
            }

            409 -> {
                RepositoryResult.Error(AuthErrorState.DUPLICATED_EMAIL)
            }

            else -> {
                RepositoryResult.Error(AuthErrorState.UNDEFINED_ERROR)
            }
        }
    }

    override suspend fun signUp(
        email: String,
        userName: String,
        password: String
    ): RepositoryResult<SignupResult, AuthErrorState> {
        val response = getApiResult {
            userAPI.register(SignupRequest(email, userName, password))
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
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

    override suspend fun login(email: String, password: String): RepositoryResult<LoginResult, AuthErrorState> {
        val response = getApiResult {
            userAPI.login(LoginRequest(email, password))
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
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

    override suspend fun deleteAccount(): RepositoryResult<Boolean, AuthErrorState> {
        val response = getApiResult {
            userAPI.deleteAccount()
        }

        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(true)
            }

            is APIResult.Error -> {
                handleError(response.code)
            }
        }
    }
}

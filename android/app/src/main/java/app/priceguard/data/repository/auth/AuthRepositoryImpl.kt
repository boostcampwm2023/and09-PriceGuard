package app.priceguard.data.repository.auth

import app.priceguard.data.dto.deleteaccount.DeleteAccountRequest
import app.priceguard.data.dto.login.LoginRequest
import app.priceguard.data.dto.password.ResetPasswordRequest
import app.priceguard.data.dto.signup.SignupRequest
import app.priceguard.data.dto.verifyemail.RequestVerificationCodeRequest
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

            401 -> {
                RepositoryResult.Error(AuthErrorState.UNAUTHORIZED)
            }

            404 -> {
                RepositoryResult.Error(AuthErrorState.NOT_FOUND)
            }

            409 -> {
                RepositoryResult.Error(AuthErrorState.DUPLICATED_EMAIL)
            }

            410 -> {
                RepositoryResult.Error(AuthErrorState.EXPIRE)
            }

            429 -> {
                RepositoryResult.Error(AuthErrorState.OVER_LIMIT)
            }

            else -> {
                RepositoryResult.Error(AuthErrorState.UNDEFINED_ERROR)
            }
        }
    }

    override suspend fun signUp(
        email: String,
        userName: String,
        password: String,
        verificationCode: String
    ): RepositoryResult<SignupResult, AuthErrorState> {
        val response = getApiResult {
            userAPI.register(SignupRequest(email, userName, verificationCode, password))
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

    override suspend fun login(
        email: String,
        password: String
    ): RepositoryResult<LoginResult, AuthErrorState> {
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

    override suspend fun deleteAccount(
        email: String,
        password: String
    ): RepositoryResult<Boolean, AuthErrorState> {
        val response = getApiResult {
            userAPI.deleteAccount(DeleteAccountRequest(email, password))
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

    override suspend fun requestVerificationCode(email: String): RepositoryResult<Boolean, AuthErrorState> {
        return when (
            val response =
                getApiResult { userAPI.requestVerificationCode(RequestVerificationCodeRequest(email)) }
        ) {
            is APIResult.Success -> {
                RepositoryResult.Success(true)
            }

            is APIResult.Error -> {
                handleError(response.code)
            }
        }
    }

    override suspend fun requestRegisterVerificationCode(email: String): RepositoryResult<Boolean, AuthErrorState> {
        return when (
            val response =
                getApiResult {
                    userAPI.requestRegisterVerificationCode(
                        RequestVerificationCodeRequest(email)
                    )
                }
        ) {
            is APIResult.Success -> {
                RepositoryResult.Success(true)
            }

            is APIResult.Error -> {
                handleError(response.code)
            }
        }
    }

    override suspend fun resetPassword(
        password: String,
        verifyToken: String
    ): RepositoryResult<Boolean, AuthErrorState> {
        return when (
            val response =
                getApiResult {
                    userAPI.resetPassword(
                        "Bearer $verifyToken",
                        ResetPasswordRequest(password)
                    )
                }
        ) {
            is APIResult.Success -> {
                RepositoryResult.Success(true)
            }

            is APIResult.Error -> {
                handleError(response.code)
            }
        }
    }
}

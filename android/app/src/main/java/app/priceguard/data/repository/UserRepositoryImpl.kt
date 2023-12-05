package app.priceguard.data.repository

import app.priceguard.data.dto.login.LoginRequest
import app.priceguard.data.dto.login.LoginState
import app.priceguard.data.dto.signup.SignupRequest
import app.priceguard.data.dto.signup.SignupState
import app.priceguard.data.network.APIResult
import app.priceguard.data.network.UserAPI
import app.priceguard.data.network.getApiResult
import app.priceguard.ui.data.LoginResult
import app.priceguard.ui.data.SignupResult
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(private val userAPI: UserAPI) : UserRepository {

    override suspend fun signUp(email: String, userName: String, password: String): SignupResult {
        val response = getApiResult {
            userAPI.register(SignupRequest(email, userName, password))
        }
        when (response) {
            is APIResult.Success -> {
                return SignupResult(SignupState.SUCCESS, response.data.accessToken, response.data.refreshToken)
            }

            is APIResult.Error -> {
                return when (response.code) {
                    400 -> {
                        SignupResult(SignupState.INVALID_PARAMETER, null, null)
                    }

                    409 -> {
                        SignupResult(SignupState.DUPLICATE_EMAIL, null, null)
                    }

                    else -> {
                        SignupResult(SignupState.UNDEFINED_ERROR, null, null)
                    }
                }
            }
        }
    }

    override suspend fun login(email: String, password: String): LoginResult {
        val response = getApiResult {
            userAPI.login(LoginRequest(email, password))
        }
        when (response) {
            is APIResult.Success -> {
                return LoginResult(LoginState.SUCCESS, response.data.accessToken, response.data.refreshToken)
            }

            is APIResult.Error -> {
                return when (response.code) {
                    400 -> {
                        LoginResult(LoginState.INVALID_PARAMETER, null, null)
                    }

                    else -> {
                        LoginResult(LoginState.UNDEFINED_ERROR, null, null)
                    }
                }
            }
        }
    }
}

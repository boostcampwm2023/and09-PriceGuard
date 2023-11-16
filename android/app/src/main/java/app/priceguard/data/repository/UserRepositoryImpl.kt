package app.priceguard.data.repository

import app.priceguard.data.dto.LoginRequest
import app.priceguard.data.dto.LoginResult
import app.priceguard.data.dto.LoginState
import app.priceguard.data.dto.SignUpRequest
import app.priceguard.data.dto.SignUpResult
import app.priceguard.data.dto.SignUpState
import app.priceguard.data.network.APIResult
import app.priceguard.data.network.RetrofitBuilder
import app.priceguard.data.network.getApiResult

class UserRepositoryImpl : UserRepository {

    private val userAPI = RetrofitBuilder.getUserRetrofit()

    override suspend fun signUp(email: String, password: String): SignUpResult {
        val response = getApiResult {
            userAPI.register(SignUpRequest(email, password))
        }
        when (response) {
            is APIResult.Success -> {
                return SignUpResult(SignUpState.SUCCESS, response.data)
            }

            is APIResult.Error -> {
                return when (response.code) {
                    400 -> {
                        SignUpResult(SignUpState.INVALID_PARAMETER)
                    }

                    409 -> {
                        SignUpResult(SignUpState.DUPLICATE_EMAIL)
                    }

                    else -> {
                        SignUpResult(SignUpState.UNDEFINED_ERROR)
                    }
                }
            }
        }
    }

    override suspend fun login(email: String, userName: String, password: String): LoginResult {
        val response = getApiResult {
            userAPI.login(LoginRequest(email, userName, password))
        }
        when (response) {
            is APIResult.Success -> {
                return LoginResult(LoginState.SUCCESS, response.data)
            }

            is APIResult.Error -> {
                return when (response.code) {
                    400 -> {
                        LoginResult(LoginState.INVALID_PARAMETER)
                    }

                    else -> {
                        LoginResult(LoginState.UNDEFINED_ERROR)
                    }
                }
            }
        }
    }
}

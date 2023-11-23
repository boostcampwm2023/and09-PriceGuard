package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val statusCode: Int,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

data class LoginResult(
    val loginState: LoginState,
    val accessToken: String?,
    val refreshToken: String?
)

enum class LoginState {
    SUCCESS,
    INVALID_PARAMETER,
    UNDEFINED_ERROR
}

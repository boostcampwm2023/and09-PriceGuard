package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val statusCode: Int,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

data class SignUpResult(
    val signUpState: SignUpState,
    val signUpResponse: SignUpResponse? = null
)

enum class SignUpState {
    SUCCESS,
    INVALID_PARAMETER,
    DUPLICATE_EMAIL,
    UNDEFINED_ERROR
}

package app.priceguard.data.dto.signup

import kotlinx.serialization.Serializable

@Serializable
data class SignupResponse(
    val statusCode: Int,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

data class SignupResult(
    val signUpState: SignupState,
    val accessToken: String?,
    val refreshToken: String?
)

enum class SignupState {
    SUCCESS,
    INVALID_PARAMETER,
    DUPLICATE_EMAIL,
    UNDEFINED_ERROR
}

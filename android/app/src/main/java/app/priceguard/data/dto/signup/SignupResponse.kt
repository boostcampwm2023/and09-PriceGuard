package app.priceguard.data.dto.signup

import kotlinx.serialization.Serializable

@Serializable
data class SignupResponse(
    val statusCode: Int,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

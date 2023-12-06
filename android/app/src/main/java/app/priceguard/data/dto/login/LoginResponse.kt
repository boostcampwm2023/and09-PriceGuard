package app.priceguard.data.dto.login

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val statusCode: Int,
    val message: String,
    val accessToken: String? = null,
    val refreshToken: String? = null
)

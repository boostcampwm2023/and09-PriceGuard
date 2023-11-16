package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val statusCode: String,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

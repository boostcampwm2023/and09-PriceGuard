package app.priceguard.data.dto

data class LoginResponse(
    val statusCode: String,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

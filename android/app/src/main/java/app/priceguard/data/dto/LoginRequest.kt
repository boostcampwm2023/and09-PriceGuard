package app.priceguard.data.dto

data class LoginRequest(
    val email: String,
    val userName: String,
    val password: String
)

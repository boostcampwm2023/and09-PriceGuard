package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val email: String,
    val userName: String,
    val password: String
)

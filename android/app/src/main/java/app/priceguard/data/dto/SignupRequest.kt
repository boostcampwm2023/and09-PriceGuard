package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val email: String,
    val userName: String,
    val password: String
)

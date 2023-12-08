package app.priceguard.data.dto.signup

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val email: String,
    val userName: String,
    val password: String
)

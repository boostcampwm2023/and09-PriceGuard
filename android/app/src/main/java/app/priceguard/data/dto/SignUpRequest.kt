package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val email: String,
    val userName: String,
    val password: String
)

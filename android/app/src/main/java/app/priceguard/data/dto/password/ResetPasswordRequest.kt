package app.priceguard.data.dto.password

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordRequest(
    val password: String
)

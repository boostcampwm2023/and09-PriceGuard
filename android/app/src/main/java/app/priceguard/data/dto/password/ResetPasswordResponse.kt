package app.priceguard.data.dto.password

import kotlinx.serialization.Serializable

@Serializable
data class ResetPasswordResponse(
    val statusCode: Int,
    val message: String
)

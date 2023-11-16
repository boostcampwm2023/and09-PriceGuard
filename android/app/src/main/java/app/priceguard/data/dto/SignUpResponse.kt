package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponse(
    val statusCode: Int,
    val message: String
)

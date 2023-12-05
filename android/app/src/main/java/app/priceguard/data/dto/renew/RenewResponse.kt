package app.priceguard.data.dto.renew

import kotlinx.serialization.Serializable

@Serializable
data class RenewResponse(
    val statusCode: Int,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

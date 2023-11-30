package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PricePatchResponse(
    val statusCode: Int,
    val message: String
)

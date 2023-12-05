package app.priceguard.data.dto.patch

import kotlinx.serialization.Serializable

@Serializable
data class PricePatchResponse(
    val statusCode: Int,
    val message: String
)

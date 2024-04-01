package app.priceguard.data.dto.patch

import kotlinx.serialization.Serializable

@Serializable
data class PricePatchRequest(
    val shop: String,
    val productCode: String,
    val targetPrice: Int
)

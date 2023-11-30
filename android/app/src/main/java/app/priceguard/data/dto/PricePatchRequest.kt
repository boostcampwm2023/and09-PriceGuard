package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PricePatchRequest(
    val productCode: String,
    val targetPrice: Int
)

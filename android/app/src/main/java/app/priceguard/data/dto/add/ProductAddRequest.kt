package app.priceguard.data.dto.add

import kotlinx.serialization.Serializable

@Serializable
data class ProductAddRequest(
    val shop: String,
    val productCode: String,
    val targetPrice: Int
)

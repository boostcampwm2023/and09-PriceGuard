package app.priceguard.data.dto.add

import kotlinx.serialization.Serializable

@Serializable
data class ProductAddRequest(
    val productCode: String,
    val targetPrice: Int
)

package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductAddRequest(
    val productCode: String,
    val targetPrice: Int
)

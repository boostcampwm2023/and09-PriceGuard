package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecommendProductDTO(
    val productName: String?,
    val productCode: String?,
    val shop: String?,
    val imageUrl: String?,
    val price: Int?,
    val rank: Int?
)

data class RecommendProductData(
    val productName: String,
    val productCode: String,
    val shop: String,
    val imageUrl: String,
    val price: Int,
    val rank: Int
)

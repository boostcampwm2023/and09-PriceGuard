package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecommendProductDTO(
    val productName: String? = null,
    val productCode: String? = null,
    val shop: String? = null,
    val imageUrl: String? = null,
    val price: Int? = null,
    val rank: Int? = null
)

data class RecommendProductData(
    val productName: String,
    val productCode: String,
    val shop: String,
    val imageUrl: String,
    val price: Int,
    val rank: Int
)

package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val statusCode: Int,
    val message: String,
    val productName: String? = null,
    val productCode: String? = null,
    val shop: String? = null,
    val imageUrl: String? = null,
    val rank: Int? = null,
    val shopUrl: String? = null,
    val targetPrice: Int? = null,
    val lowestPrice: Int? = null,
    val price: Int? = null
)

data class ProductDetailResult(
    val productName: String,
    val productCode: String,
    val shop: String,
    val imageUrl: String,
    val rank: Int,
    val shopUrl: String,
    val targetPrice: Int,
    val lowestPrice: Int,
    val price: Int
)

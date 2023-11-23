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
    val state: ProductDetailState,
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

enum class ProductDetailState {
    SUCCESS,
    PERMISSION_DENIED,
    NOT_FOUND,
    UNDEFINED_ERROR
}

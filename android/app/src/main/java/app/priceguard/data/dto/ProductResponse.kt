package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val statusCode: Int,
    val message: String,
    val productName: String? = null,
    val productCode: String? = null,
    val productPrice: Int? = null,
    val shop: String? = null,
    val imageUrl: String? = null
)

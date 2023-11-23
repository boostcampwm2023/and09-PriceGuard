package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductVerifyResponse(
    val statusCode: Int,
    val message: String,
    val productName: String?,
    val productCode: String?,
    val productPrice: Int?,
    val shop: String?,
    val imageUrl: String?
)

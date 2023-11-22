package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val statusCode: Int,
    val message: String,
    val productName: String?,
    val productCode: String?,
    val shop: String?,
    val imageUrl: String?
)

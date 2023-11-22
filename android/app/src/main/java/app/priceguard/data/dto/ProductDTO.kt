package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDTO(
    val productName: String?,
    val productCode: String?,
    val shop: String?,
    val imageUrl: String?
)

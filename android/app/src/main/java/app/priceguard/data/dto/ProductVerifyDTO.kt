package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductVerifyDTO(
    val productName: String? = null,
    val productCode: String? = null,
    val productPrice: Int? = null,
    val shop: String? = null,
    val imageUrl: String? = null
)

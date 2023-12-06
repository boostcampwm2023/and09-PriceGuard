package app.priceguard.data.dto.list

import kotlinx.serialization.Serializable

@Serializable
data class ProductListResponse(
    val statusCode: Int,
    val message: String,
    val trackingList: List<ProductDTO>? = null
)

package app.priceguard.data.dto.add

import kotlinx.serialization.Serializable

@Serializable
data class ProductAddResponse(
    val statusCode: Int,
    val message: String
)

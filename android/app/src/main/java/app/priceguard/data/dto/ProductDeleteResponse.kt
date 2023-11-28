package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDeleteResponse(
    val statusCode: Int,
    val message: String
)

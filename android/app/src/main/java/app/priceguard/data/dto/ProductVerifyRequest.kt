package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductVerifyRequest(
    val productUrl: String
)

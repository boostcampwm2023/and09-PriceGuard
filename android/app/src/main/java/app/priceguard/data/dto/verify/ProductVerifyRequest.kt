package app.priceguard.data.dto.verify

import kotlinx.serialization.Serializable

@Serializable
data class ProductVerifyRequest(
    val productUrl: String
)

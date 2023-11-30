package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PriceDataDTO(
    val time: Float? = null,
    val price: Int? = null,
    val isSoldOut: Boolean? = null
)

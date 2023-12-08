package app.priceguard.data.dto.detail

import app.priceguard.data.dto.PriceDataDTO
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponse(
    val statusCode: Int,
    val message: String,
    val productName: String? = null,
    val productCode: String? = null,
    val shop: String? = null,
    val imageUrl: String? = null,
    val rank: Int? = null,
    val shopUrl: String? = null,
    val targetPrice: Int? = null,
    val lowestPrice: Int? = null,
    val price: Int? = null,
    val priceData: List<PriceDataDTO>? = null
)

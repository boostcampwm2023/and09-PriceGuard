package app.priceguard.data.dto.recommend

import app.priceguard.data.dto.PriceDataDTO
import kotlinx.serialization.Serializable

@Serializable
data class RecommendProductDTO(
    val productName: String? = null,
    val productCode: String? = null,
    val shop: String? = null,
    val imageUrl: String? = null,
    val price: Int? = null,
    val rank: Int? = null,
    val priceData: List<PriceDataDTO>? = null
)

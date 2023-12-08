package app.priceguard.data.dto.list

import app.priceguard.data.dto.PriceDataDTO
import kotlinx.serialization.Serializable

@Serializable
data class ProductDTO(
    val productName: String? = null,
    val productCode: String? = null,
    val shop: String? = null,
    val imageUrl: String? = null,
    val targetPrice: Int? = null,
    val price: Int? = null,
    val isAlert: Boolean? = null,
    val priceData: List<PriceDataDTO>? = null
)

package app.priceguard.data.dto

import app.priceguard.data.graph.ProductChartData
import kotlinx.serialization.Serializable

@Serializable
data class ProductDTO(
    val productName: String? = null,
    val productCode: String? = null,
    val shop: String? = null,
    val imageUrl: String? = null,
    val targetPrice: Int? = null,
    val price: Int? = null,
    val priceData: List<PriceDataDTO>? = null
)

data class ProductData(
    val productName: String,
    val productCode: String,
    val shop: String,
    val imageUrl: String,
    val targetPrice: Int,
    val price: Int,
    val priceData: List<ProductChartData>
)

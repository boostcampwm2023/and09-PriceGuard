package app.priceguard.ui.data

import app.priceguard.data.graph.ProductChartData

data class RecommendProductData(
    val productName: String,
    val productCode: String,
    val shop: String,
    val imageUrl: String,
    val price: Int,
    val rank: Int,
    val priceData: List<ProductChartData>
)

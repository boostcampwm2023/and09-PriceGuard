package app.priceguard.ui.data

import app.priceguard.data.graph.ProductChartData

data class ProductDetailResult(
    val productName: String,
    val productCode: String,
    val shop: String,
    val imageUrl: String,
    val rank: Int,
    val shopUrl: String,
    val targetPrice: Int,
    val lowestPrice: Int,
    val price: Int,
    val priceData: List<ProductChartData>
)

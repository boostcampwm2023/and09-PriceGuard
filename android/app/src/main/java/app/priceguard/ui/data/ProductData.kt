package app.priceguard.ui.data

import app.priceguard.data.graph.ProductChartData

data class ProductData(
    val productName: String,
    val productCode: String,
    val shop: String,
    val imageUrl: String,
    val targetPrice: Int,
    val price: Int,
    val priceData: List<ProductChartData>
)

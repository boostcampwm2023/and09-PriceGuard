package app.priceguard.ui.home

import app.priceguard.data.graph.ProductChartData

sealed interface ProductSummary {
    val brandType: String
    val title: String
    val price: Int
    val productCode: String
    val priceData: List<ProductChartData>

    data class UserProductSummary(
        override val brandType: String,
        override val title: String,
        override val price: Int,
        override val productCode: String,
        override val priceData: List<ProductChartData>,
        val discountPercent: Float,
        val isAlarmOn: Boolean
    ) : ProductSummary

    data class RecommendedProductSummary(
        override val brandType: String,
        override val title: String,
        override val price: Int,
        override val productCode: String,
        override val priceData: List<ProductChartData>,
        val recommendRank: Int
    ) : ProductSummary
}

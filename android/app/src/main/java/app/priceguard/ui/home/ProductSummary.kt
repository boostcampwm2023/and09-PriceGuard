package app.priceguard.ui.home

sealed interface ProductSummary {
    val brandType: String
    val title: String
    val price: Int
    val productCode: String

    data class UserProductSummary(
        override val brandType: String,
        override val title: String,
        override val price: Int,
        override val productCode: String,
        val discountPercent: Float,
        val isAlarmOn: Boolean
    ) : ProductSummary

    data class RecommendedProductSummary(
        override val brandType: String,
        override val title: String,
        override val price: Int,
        override val productCode: String,
        val recommendRank: Int
    ) : ProductSummary
}

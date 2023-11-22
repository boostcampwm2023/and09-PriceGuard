package app.priceguard.ui.home

sealed interface ProductSummary {
    val brandType: String
    val title: String
    val price: String
    val discountPercent: String

    data class UserProductSummary(
        override val brandType: String,
        override val title: String,
        override val price: String,
        override val discountPercent: String,
        val isAlarmOn: Boolean
    ) : ProductSummary

    data class RecommendedProductSummary(
        override val brandType: String,
        override val title: String,
        override val price: String,
        override val discountPercent: String,
        val recommendRank: Int
    ) : ProductSummary
}

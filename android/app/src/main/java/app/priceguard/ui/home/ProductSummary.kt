package app.priceguard.ui.home

sealed interface ProductSummary {

    fun readTitle(): String

    fun readPrice(): String

    fun readDiscountPercent(): String

    data class UserProductSummary(
        val brandType: String,
        val title: String,
        val price: String,
        val discountPercent: String,
        val isAlarmOn: Boolean
    ) : ProductSummary {

        override fun readTitle(): String = title

        override fun readPrice(): String = price

        override fun readDiscountPercent(): String = discountPercent
    }

    data class RecommendedProductSummary(
        val brandType: String,
        val title: String,
        val price: String,
        val discountPercent: String,
        val recommendRank: Int
    ) : ProductSummary {

        override fun readTitle(): String = title

        override fun readPrice(): String = price

        override fun readDiscountPercent(): String = discountPercent
    }
}

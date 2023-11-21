package app.priceguard.ui.home

import java.util.UUID

sealed interface ProductSummary {

    fun readId(): UUID

    fun readTitle(): String

    fun readPrice(): String

    fun readDiscountPercent(): String

    data class UserProductSummary(
        val brandType: String,
        val title: String,
        val price: String,
        val discountPercent: String,
        val isAlarmOn: Boolean,
        val id: UUID = UUID.randomUUID()
    ) : ProductSummary {
        override fun readId(): UUID = id

        override fun readTitle(): String = title

        override fun readPrice(): String = price

        override fun readDiscountPercent(): String = discountPercent
    }

    data class RecommendedProductSummary(
        val brandType: String,
        val title: String,
        val price: String,
        val discountPercent: String,
        val recommendRank: Int,
        val id: UUID = UUID.randomUUID()
    ) : ProductSummary {
        override fun readId(): UUID = id

        override fun readTitle(): String = title

        override fun readPrice(): String = price

        override fun readDiscountPercent(): String = discountPercent
    }
}

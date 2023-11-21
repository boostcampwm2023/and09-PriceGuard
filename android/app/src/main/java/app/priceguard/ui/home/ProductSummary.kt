package app.priceguard.ui.home

import java.util.*

enum class BrandType {
    ELEVENST
}

data class ProductSummary(
    val brandType: BrandType,
    val title: String,
    val price: String,
    val discountPercent: String,
    val isAddedToList: Boolean,
    val isAlarmOn: Boolean,
    val id: String = UUID.randomUUID().toString()
)

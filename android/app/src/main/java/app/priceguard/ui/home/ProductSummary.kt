package app.priceguard.ui.home

enum class BrandType {
    ELEVENST
}

data class ProductSummary(
    val brandType: BrandType,
    val title: String,
    val price: String,
    val discountPercent: String,
    val isAddedToList: Boolean,
    val isAlarmOn: Boolean
)

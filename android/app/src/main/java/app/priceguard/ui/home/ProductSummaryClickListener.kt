package app.priceguard.ui.home

interface ProductSummaryClickListener {
    fun onClick(productShop: String, productCode: String)

    fun onToggle(productShop: String, productCode: String, checked: Boolean)
}

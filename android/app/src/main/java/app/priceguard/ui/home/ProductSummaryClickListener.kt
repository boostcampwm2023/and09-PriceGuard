package app.priceguard.ui.home

interface ProductSummaryClickListener {
    fun onClick(productCode: String)

    fun onToggle(productCode: String, checked: Boolean)
}

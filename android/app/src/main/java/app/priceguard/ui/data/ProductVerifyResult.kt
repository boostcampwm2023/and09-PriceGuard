package app.priceguard.ui.data

data class ProductVerifyResult(
    val productName: String,
    val productCode: String,
    val productPrice: Int,
    val shop: String,
    val imageUrl: String
)

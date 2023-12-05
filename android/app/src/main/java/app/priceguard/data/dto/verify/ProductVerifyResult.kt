package app.priceguard.data.dto.verify

data class ProductVerifyResult(
    val productName: String,
    val productCode: String,
    val productPrice: Int,
    val shop: String,
    val imageUrl: String
)

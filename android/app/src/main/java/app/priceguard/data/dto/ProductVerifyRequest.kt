package app.priceguard.data.dto

data class ProductVerifyRequest(
    val productUrl: String
)

data class ProductResponse(
    val statusCode: Int,
    val message: String,
    val productName: String?,
    val productCode: String?,
    val shop: String?,
    val imageUrl: String?
)

data class Product(
    val productName: String?,
    val productCode: String?,
    val shop: String?,
    val imageUrl: String?
)

data class ProductAddRequest(
    val productCode: String,
    val targetPrice: String
)

data class ProductListResponse(
    val statusCode: Int,
    val message: String,
    val trackingList: List<Product>?
)

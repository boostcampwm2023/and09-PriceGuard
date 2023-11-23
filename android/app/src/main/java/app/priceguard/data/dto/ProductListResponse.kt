package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductListResponse(
    val statusCode: Int,
    val message: String,
    val trackingList: List<ProductDTO>?
)

data class ProductListResult(
    val productListState: ProductListState,
    val trackingList: List<ProductData>
)

enum class ProductListState {
    SUCCESS,
    PERMISSION_DENIED,
    NOT_FOUND,
    UNDEFINED_ERROR
}

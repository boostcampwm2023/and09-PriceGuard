package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecommendProductResponse(
    val statusCode: Int,
    val message: String,
    val recommendList: List<RecommendProductDTO>?
)

data class RecommendProductResult(
    val productListState: RecommendProductState,
    val recommendList: List<RecommendProductData>
)

enum class RecommendProductState {
    SUCCESS,
    WRONG_REQUEST,
    PERMISSION_DENIED,
    NOT_FOUND,
    UNDEFINED_ERROR
}

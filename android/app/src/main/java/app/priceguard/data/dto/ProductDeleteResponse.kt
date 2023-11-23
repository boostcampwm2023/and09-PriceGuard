package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductDeleteResponse(
    val statusCode: Int,
    val message: String
)

enum class ProductDeleteState {
    SUCCESS,
    NOT_FOUND,
    INVALID_REQUEST,
    UNAUTHORIZED,
    UNDEFINED_ERROR
}

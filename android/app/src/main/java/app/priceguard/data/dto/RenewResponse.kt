package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RenewResponse(
    val statusCode: Int,
    val message: String,
    val accessToken: String,
    val refreshToken: String
)

enum class RenewResult {
    SUCCESS,
    EXPIRED,
    UNAUTHORIZED,
    UNKNOWN_ERROR
}
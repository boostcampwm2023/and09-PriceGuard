package app.priceguard.data.dto.isverified

import kotlinx.serialization.Serializable

@Serializable
data class IsEmailVerifiedResponse(
    val statusCode: Int,
    val message: String,
    val verified: Boolean? = null
)

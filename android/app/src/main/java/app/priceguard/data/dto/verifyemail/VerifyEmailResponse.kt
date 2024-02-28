package app.priceguard.data.dto.verifyemail

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailResponse(
    val statusCode: Int,
    val message: String,
    val verifyToken: String? = null
)

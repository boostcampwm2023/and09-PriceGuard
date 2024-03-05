package app.priceguard.data.dto.verifyemail

import kotlinx.serialization.Serializable

@Serializable
data class RequestVerificationCodeResponse(
    val statusCode: Int,
    val message: String
)

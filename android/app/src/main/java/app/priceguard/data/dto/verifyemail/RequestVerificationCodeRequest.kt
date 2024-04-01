package app.priceguard.data.dto.verifyemail

import kotlinx.serialization.Serializable

@Serializable
data class RequestVerificationCodeRequest(
    val email: String
)

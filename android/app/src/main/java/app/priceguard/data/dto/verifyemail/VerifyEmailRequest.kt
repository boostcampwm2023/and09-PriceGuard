package app.priceguard.data.dto.verifyemail

import kotlinx.serialization.Serializable

@Serializable
data class VerifyEmailRequest(
    val email: String,
    val verificationCode: String
)

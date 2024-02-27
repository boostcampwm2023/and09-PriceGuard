package app.priceguard.data.dto.verifyemail

data class VerifyEmailRequest(
    val email: String,
    val verificationCode: String
)

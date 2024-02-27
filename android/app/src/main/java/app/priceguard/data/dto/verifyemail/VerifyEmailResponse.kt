package app.priceguard.data.dto.verifyemail

data class VerifyEmailResponse(
    val statusCode: Int,
    val message: String,
    val verifyToken: String? = null
)

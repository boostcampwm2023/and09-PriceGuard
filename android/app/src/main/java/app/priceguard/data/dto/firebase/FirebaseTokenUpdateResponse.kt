package app.priceguard.data.dto.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseTokenUpdateResponse(
    val statusCode: Int,
    val message: String
)

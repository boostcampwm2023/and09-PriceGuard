package app.priceguard.data.dto.firebase

import kotlinx.serialization.Serializable

@Serializable
data class FirebaseTokenUpdateRequest(
    val token: String
)

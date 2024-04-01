package app.priceguard.data.dto.deleteaccount

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountRequest(
    val email: String,
    val password: String
)

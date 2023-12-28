package app.priceguard.data.dto.deleteaccount

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountResponse(
    val statusCode: Int,
    val message: String
)

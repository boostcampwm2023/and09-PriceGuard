package app.priceguard.data.dto.alert

import kotlinx.serialization.Serializable

@Serializable
data class AlertUpdateResponse(
    val statusCode: Int,
    val message: String
)

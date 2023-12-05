package app.priceguard.data.repository

import kotlinx.serialization.Serializable

@Serializable
data class TokenUserData(
    val id: String,
    val email: String,
    val name: String,
    val iat: Int,
    val exp: Int
)

package app.priceguard.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDataDTO(
    val id: String,
    val email: String,
    val name: String,
    val iat: Int,
    val exp: Int
)

data class UserDataResult(
    val email: String,
    val name: String
)

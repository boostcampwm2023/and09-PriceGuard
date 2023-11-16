package app.priceguard.data.repository

import app.priceguard.data.dto.LoginResponse
import app.priceguard.data.dto.SignUpResponse

interface SignUpRepository {
    suspend fun signUp(email: String, password: String): SignUpResponse
    suspend fun login(email: String, userName: String, password: String): LoginResponse
}

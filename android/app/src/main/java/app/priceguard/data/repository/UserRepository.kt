package app.priceguard.data.repository

import app.priceguard.data.dto.LoginResult
import app.priceguard.data.dto.SignUpResult

interface UserRepository {

    suspend fun signUp(email: String, userName: String, password: String): SignUpResult

    suspend fun login(email: String, password: String): LoginResult
}

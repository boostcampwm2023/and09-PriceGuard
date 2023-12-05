package app.priceguard.data.repository

import app.priceguard.data.dto.login.LoginResult
import app.priceguard.data.dto.signup.SignupResult

interface UserRepository {

    suspend fun signUp(email: String, userName: String, password: String): SignupResult

    suspend fun login(email: String, password: String): LoginResult
}

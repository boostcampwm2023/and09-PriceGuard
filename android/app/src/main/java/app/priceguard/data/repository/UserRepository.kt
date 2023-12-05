package app.priceguard.data.repository

import app.priceguard.ui.data.LoginResult
import app.priceguard.ui.data.SignupResult

interface UserRepository {

    suspend fun signUp(email: String, userName: String, password: String): SignupResult

    suspend fun login(email: String, password: String): LoginResult
}

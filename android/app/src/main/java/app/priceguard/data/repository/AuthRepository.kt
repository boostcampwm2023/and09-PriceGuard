package app.priceguard.data.repository

import app.priceguard.ui.data.LoginResult
import app.priceguard.ui.data.SignupResult

interface AuthRepository {

    suspend fun signUp(email: String, userName: String, password: String): AuthRepositoryResult<SignupResult>

    suspend fun login(email: String, password: String): AuthRepositoryResult<LoginResult>
}

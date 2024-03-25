package app.priceguard.data.repository.auth

import app.priceguard.data.repository.RepositoryResult
import app.priceguard.ui.data.LoginResult
import app.priceguard.ui.data.SignupResult

interface AuthRepository {

    suspend fun signUp(email: String, userName: String, password: String, verificationCode: String): RepositoryResult<SignupResult, AuthErrorState>

    suspend fun login(email: String, password: String): RepositoryResult<LoginResult, AuthErrorState>

    suspend fun deleteAccount(email: String, password: String): RepositoryResult<Boolean, AuthErrorState>

    suspend fun requestVerificationCode(email: String): RepositoryResult<Boolean, AuthErrorState>

    suspend fun requestRegisterVerificationCode(email: String): RepositoryResult<Boolean, AuthErrorState>

    suspend fun resetPassword(password: String, verifyToken: String): RepositoryResult<Boolean, AuthErrorState>
}

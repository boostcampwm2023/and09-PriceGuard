package app.priceguard.data.repository

import app.priceguard.data.dto.LoginResponse
import app.priceguard.data.dto.SignUpResponse

class SignUpRepositoryImpl : SignUpRepository {
    override suspend fun signUp(email: String, password: String): SignUpResponse {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, userName: String, password: String): LoginResponse {
        TODO("Not yet implemented")
    }
}

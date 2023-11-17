package app.priceguard.data.network

import app.priceguard.data.dto.LoginRequest
import app.priceguard.data.dto.LoginResponse
import app.priceguard.data.dto.SignUpRequest
import app.priceguard.data.dto.SignUpResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserAPI {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("register")
    suspend fun register(
        @Body request: SignUpRequest
    ): Response<SignUpResponse>
}

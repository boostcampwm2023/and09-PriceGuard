package app.priceguard.data.network

import app.priceguard.data.dto.login.LoginRequest
import app.priceguard.data.dto.login.LoginResponse
import app.priceguard.data.dto.signup.SignupRequest
import app.priceguard.data.dto.signup.SignupResponse
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
        @Body request: SignupRequest
    ): Response<SignupResponse>
}

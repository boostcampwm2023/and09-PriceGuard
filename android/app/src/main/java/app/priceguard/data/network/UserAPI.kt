package app.priceguard.data.network

import app.priceguard.data.dto.deleteaccount.DeleteAccountResponse
import app.priceguard.data.dto.firebase.FirebaseTokenUpdateRequest
import app.priceguard.data.dto.firebase.FirebaseTokenUpdateResponse
import app.priceguard.data.dto.login.LoginRequest
import app.priceguard.data.dto.login.LoginResponse
import app.priceguard.data.dto.signup.SignupRequest
import app.priceguard.data.dto.signup.SignupResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserAPI {

    @POST("login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("register")
    suspend fun register(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    @DELETE("")
    suspend fun deleteAccount(): Response<DeleteAccountResponse>

    @PUT("firebase/token")
    suspend fun updateFirebaseToken(
        @Header("Authorization") authToken: String,
        @Body request: FirebaseTokenUpdateRequest
    ): Response<FirebaseTokenUpdateResponse>
}

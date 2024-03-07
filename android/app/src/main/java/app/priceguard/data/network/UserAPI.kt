package app.priceguard.data.network

import app.priceguard.data.dto.deleteaccount.DeleteAccountRequest
import app.priceguard.data.dto.deleteaccount.DeleteAccountResponse
import app.priceguard.data.dto.firebase.FirebaseTokenUpdateRequest
import app.priceguard.data.dto.firebase.FirebaseTokenUpdateResponse
import app.priceguard.data.dto.isverified.IsEmailVerifiedResponse
import app.priceguard.data.dto.login.LoginRequest
import app.priceguard.data.dto.login.LoginResponse
import app.priceguard.data.dto.password.ResetPasswordRequest
import app.priceguard.data.dto.password.ResetPasswordResponse
import app.priceguard.data.dto.signup.SignupRequest
import app.priceguard.data.dto.signup.SignupResponse
import app.priceguard.data.dto.verifyemail.RequestVerificationCodeRequest
import app.priceguard.data.dto.verifyemail.RequestVerificationCodeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
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

    @POST("remove")
    suspend fun deleteAccount(
        @Body request: DeleteAccountRequest
    ): Response<DeleteAccountResponse>

    @PUT("firebase/token")
    suspend fun updateFirebaseToken(
        @Header("Authorization") authToken: String,
        @Body request: FirebaseTokenUpdateRequest
    ): Response<FirebaseTokenUpdateResponse>

    @GET("email/is-verified")
    suspend fun updateIsEmailVerified(
        @Header("Authorization") accessToken: String
    ): Response<IsEmailVerifiedResponse>

    @POST("email/verification")
    suspend fun requestVerificationCode(
        @Body requestVerificationCodeRequest: RequestVerificationCodeRequest
    ): Response<RequestVerificationCodeResponse>

    @POST("password")
    suspend fun resetPassword(
        @Header("Authorization") token: String,
        @Body resetPasswordRequest: ResetPasswordRequest
    ): Response<ResetPasswordResponse>
}

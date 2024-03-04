package app.priceguard.data.network

import app.priceguard.data.dto.renew.RenewResponse
import app.priceguard.data.dto.verifyemail.VerifyEmailRequest
import app.priceguard.data.dto.verifyemail.VerifyEmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthAPI {

    @GET("refreshJWT")
    suspend fun renewTokens(
        @Header("Authorization") authToken: String
    ): Response<RenewResponse>

    @POST("verify/email")
    suspend fun verifyEmail(
        @Body verifyEmailRequest: VerifyEmailRequest
    ): Response<VerifyEmailResponse>
}

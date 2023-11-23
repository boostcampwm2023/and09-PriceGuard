package app.priceguard.data.network

import app.priceguard.data.dto.RenewResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface AuthAPI {

    @GET("refreshJWT")
    suspend fun renewTokens(
        @Header("Authorization") authToken: String
    ): Response<RenewResponse>
}

package app.priceguard.data.network

import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductDeleteResponse
import app.priceguard.data.dto.ProductListResponse
import app.priceguard.data.dto.ProductResponse
import app.priceguard.data.dto.ProductVerifyRequest
import app.priceguard.data.dto.RecommendProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductAPI {

    @POST("verify")
    suspend fun verifyLink(
        @Body productUrl: ProductVerifyRequest
    ): Response<ProductResponse>

    @POST
    suspend fun addProduct(
        @Body productAddRequest: ProductAddRequest
    ): Response<ProductResponse>

    @GET("tracking")
    suspend fun getProductList(): Response<ProductListResponse>

    @GET("recommend")
    suspend fun getRecommendedProductList(): Response<RecommendProductResponse>

    @GET("{productCode}")
    suspend fun getProductDetail(
        @Path("productCode") productCode: String
    ): Response<ProductResponse>

    @DELETE("{productCode}")
    suspend fun deleteProduct(
        @Path("productCode") productCode: String
    ): Response<ProductDeleteResponse>

    @PATCH("targetPrice")
    suspend fun updateTargetPrice(
        @Body productAddRequest: ProductAddRequest
    ): Response<ProductResponse>
}

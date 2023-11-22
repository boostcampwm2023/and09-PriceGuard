package app.priceguard.data.network

import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductListResponse
import app.priceguard.data.dto.ProductResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductAPI {

    @POST("product/verify")
    suspend fun verifyLink(
        @Body productUrl: String
    ): Response<ProductResponse>

    @POST("product")
    suspend fun addProduct(
        @Body productAddRequest: ProductAddRequest
    ): Response<ProductResponse>

    @GET("product/tracking")
    suspend fun getProductList(): Response<ProductListResponse>

    @GET("product/recommend")
    suspend fun getRecommendedProductList(): Response<ProductListResponse>

    @GET("product/{productCode}")
    suspend fun getProductDetail(
        @Path("productCode") productCode: String
    ): Response<ProductResponse>

    @DELETE("product/{productCode}")
    suspend fun deleteProduct(
        @Path("productCode") productCode: String
    ): Response<ProductResponse>

    @PATCH("product/targetPrice")
    suspend fun updateTargetPrice(
        @Body productAddRequest: ProductAddRequest
    ): Response<ProductResponse>
}

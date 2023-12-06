package app.priceguard.data.network

import app.priceguard.data.dto.add.ProductAddRequest
import app.priceguard.data.dto.add.ProductAddResponse
import app.priceguard.data.dto.delete.ProductDeleteResponse
import app.priceguard.data.dto.detail.ProductResponse
import app.priceguard.data.dto.list.ProductListResponse
import app.priceguard.data.dto.patch.PricePatchRequest
import app.priceguard.data.dto.patch.PricePatchResponse
import app.priceguard.data.dto.recommend.RecommendProductResponse
import app.priceguard.data.dto.verify.ProductVerifyRequest
import app.priceguard.data.dto.verify.ProductVerifyResponse
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
    ): Response<ProductVerifyResponse>

    @POST(".")
    suspend fun addProduct(
        @Body productAddRequest: ProductAddRequest
    ): Response<ProductAddResponse>

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
        @Body pricePatchRequest: PricePatchRequest
    ): Response<PricePatchResponse>
}

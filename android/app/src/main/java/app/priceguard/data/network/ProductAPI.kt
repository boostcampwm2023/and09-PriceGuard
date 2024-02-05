package app.priceguard.data.network

import app.priceguard.data.dto.add.ProductAddRequest
import app.priceguard.data.dto.add.ProductAddResponse
import app.priceguard.data.dto.alert.AlertUpdateResponse
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

    @POST("v1/product/verify")
    suspend fun verifyLink(
        @Body productUrl: ProductVerifyRequest
    ): Response<ProductVerifyResponse>

    @POST("v1/product")
    suspend fun addProduct(
        @Body productAddRequest: ProductAddRequest
    ): Response<ProductAddResponse>

    @GET("product/tracking")
    suspend fun getProductList(): Response<ProductListResponse>

    @GET("product/recommend")
    suspend fun getRecommendedProductList(): Response<RecommendProductResponse>

    @GET("v1/product/{shop}/{productCode}")
    suspend fun getProductDetail(
        @Path("shop") shop: String,
        @Path("productCode") productCode: String
    ): Response<ProductResponse>

    @DELETE("v1/product/{shop}/{productCode}")
    suspend fun deleteProduct(
        @Path("shop") shop: String,
        @Path("productCode") productCode: String
    ): Response<ProductDeleteResponse>

    @PATCH("v1/product/targetPrice")
    suspend fun updateTargetPrice(
        @Body pricePatchRequest: PricePatchRequest
    ): Response<PricePatchResponse>

    @PATCH("v1/product/alert/{shop}/{productCode}")
    suspend fun updateAlert(
        @Path("shop") shop: String,
        @Path("productCode") productCode: String
    ): Response<AlertUpdateResponse>
}

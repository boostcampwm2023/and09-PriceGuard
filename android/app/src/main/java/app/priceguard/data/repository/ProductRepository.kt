package app.priceguard.data.repository

import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductDeleteState
import app.priceguard.data.dto.ProductDetailResult
import app.priceguard.data.dto.ProductListResult
import app.priceguard.data.dto.ProductResponse
import app.priceguard.data.dto.ProductVerifyRequest
import app.priceguard.data.network.APIResult
import app.priceguard.data.dto.RecommendProductResult

interface ProductRepository {

    suspend fun verifyLink(productUrl: ProductVerifyRequest): APIResult<ProductResponse>

    suspend fun addProduct(productAddRequest: ProductAddRequest): APIResult<ProductResponse>

    suspend fun getProductList(afterRenew: Boolean = false): ProductListResult

    suspend fun getRecommendedProductList(afterRenew: Boolean = false): RecommendProductResult

    suspend fun getProductDetail(productCode: String, renewed: Boolean): ProductDetailResult

    suspend fun deleteProduct(productCode: String, renewed: Boolean): ProductDeleteState

    suspend fun updateTargetPrice(productAddRequest: ProductAddRequest): ProductResponse
}

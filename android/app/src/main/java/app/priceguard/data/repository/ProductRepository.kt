package app.priceguard.data.repository

import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductDetailResult
import app.priceguard.data.dto.ProductListResult
import app.priceguard.data.dto.ProductResponse
import app.priceguard.data.dto.ProductVerifyRequest

interface ProductRepository {

    suspend fun verifyLink(productUrl: ProductVerifyRequest): ProductResponse

    suspend fun addProduct(productAddRequest: ProductAddRequest): ProductResponse

    suspend fun getProductList(afterRenew: Boolean = false): ProductListResult

    suspend fun getRecommendedProductList(afterRenew: Boolean = false): ProductListResult

    suspend fun getProductDetail(productCode: String, renewed: Boolean): ProductDetailResult

    suspend fun deleteProduct(productCode: String): ProductResponse

    suspend fun updateTargetPrice(productAddRequest: ProductAddRequest): ProductResponse
}

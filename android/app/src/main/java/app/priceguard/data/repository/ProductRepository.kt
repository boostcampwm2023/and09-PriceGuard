package app.priceguard.data.repository

import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductListResponse
import app.priceguard.data.dto.ProductResponse

interface ProductRepository {

    suspend fun verifyLink(productUrl: String): ProductResponse

    suspend fun addProduct(productAddRequest: ProductAddRequest): ProductResponse

    suspend fun getProductList(): ProductListResponse

    suspend fun getRecommendedProductList(): ProductListResponse

    suspend fun getProductDetail(productCode: String): ProductResponse

    suspend fun deleteProduct(productCode: String): ProductResponse

    suspend fun updateTargetPrice(productAddRequest: ProductAddRequest): ProductResponse
}

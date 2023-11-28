package app.priceguard.data.repository

import app.priceguard.data.dto.PricePatchRequest
import app.priceguard.data.dto.PricePatchResponse
import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductAddResponse
import app.priceguard.data.dto.ProductData
import app.priceguard.data.dto.ProductDetailResult
import app.priceguard.data.dto.ProductVerifyDTO
import app.priceguard.data.dto.ProductVerifyRequest
import app.priceguard.data.dto.RecommendProductData
import app.priceguard.data.network.ProductRepositoryResult

interface ProductRepository {

    suspend fun verifyLink(productUrl: ProductVerifyRequest, isRenewed: Boolean = false): ProductRepositoryResult<ProductVerifyDTO>

    suspend fun addProduct(productAddRequest: ProductAddRequest, isRenewed: Boolean = false): ProductRepositoryResult<ProductAddResponse>

    suspend fun getProductList(isRenewed: Boolean = false): ProductRepositoryResult<List<ProductData>>

    suspend fun getRecommendedProductList(isRenewed: Boolean = false): ProductRepositoryResult<List<RecommendProductData>>

    suspend fun getProductDetail(productCode: String, isRenewed: Boolean = false): ProductRepositoryResult<ProductDetailResult>

    suspend fun deleteProduct(productCode: String, isRenewed: Boolean = false): ProductRepositoryResult<Boolean>

    suspend fun updateTargetPrice(pricePatchRequest: PricePatchRequest, isRenewed: Boolean = false): ProductRepositoryResult<PricePatchResponse>
}

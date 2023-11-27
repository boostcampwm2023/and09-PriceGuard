package app.priceguard.data.repository

import app.priceguard.data.dto.PricePatchRequest
import app.priceguard.data.dto.PricePatchResponse
import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductAddResponse
import app.priceguard.data.dto.ProductData
import app.priceguard.data.dto.ProductDeleteState
import app.priceguard.data.dto.ProductDetailResult
import app.priceguard.data.dto.ProductVerifyDTO
import app.priceguard.data.dto.ProductVerifyRequest
import app.priceguard.data.dto.RecommendProductData
import app.priceguard.data.network.RepositoryResult

interface ProductRepository {

    suspend fun verifyLink(productUrl: ProductVerifyRequest, isRenewed: Boolean): RepositoryResult<ProductVerifyDTO>

    suspend fun addProduct(productAddRequest: ProductAddRequest, isRenewed: Boolean): RepositoryResult<ProductAddResponse>

    suspend fun getProductList(afterRenew: Boolean = false): RepositoryResult<List<ProductData>>

    suspend fun getRecommendedProductList(afterRenew: Boolean = false): RepositoryResult<List<RecommendProductData>>

    suspend fun getProductDetail(productCode: String, renewed: Boolean): RepositoryResult<ProductDetailResult>

    suspend fun deleteProduct(productCode: String, renewed: Boolean): ProductDeleteState

    suspend fun updateTargetPrice(pricePatchRequest: PricePatchRequest, isRenewed: Boolean): RepositoryResult<PricePatchResponse>
}

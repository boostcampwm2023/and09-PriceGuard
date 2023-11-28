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

    suspend fun verifyLink(productUrl: ProductVerifyRequest, isRenewed: Boolean = false): RepositoryResult<ProductVerifyDTO>

    suspend fun addProduct(productAddRequest: ProductAddRequest, isRenewed: Boolean = false): RepositoryResult<ProductAddResponse>

    suspend fun getProductList(isRenewed: Boolean = false): RepositoryResult<List<ProductData>>

    suspend fun getRecommendedProductList(isRenewed: Boolean = false): RepositoryResult<List<RecommendProductData>>

    suspend fun getProductDetail(productCode: String, isRenewed: Boolean = false): RepositoryResult<ProductDetailResult>

    suspend fun deleteProduct(productCode: String, isRenewed: Boolean = false): ProductDeleteState

    suspend fun updateTargetPrice(pricePatchRequest: PricePatchRequest, isRenewed: Boolean = false): RepositoryResult<PricePatchResponse>
}

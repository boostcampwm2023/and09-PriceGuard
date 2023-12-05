package app.priceguard.data.repository

import app.priceguard.data.dto.add.ProductAddRequest
import app.priceguard.data.dto.add.ProductAddResponse
import app.priceguard.data.dto.detail.ProductDetailResult
import app.priceguard.data.dto.list.ProductData
import app.priceguard.data.dto.patch.PricePatchRequest
import app.priceguard.data.dto.patch.PricePatchResponse
import app.priceguard.data.dto.recommend.RecommendProductData
import app.priceguard.data.dto.verify.ProductVerifyDTO
import app.priceguard.data.dto.verify.ProductVerifyRequest
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

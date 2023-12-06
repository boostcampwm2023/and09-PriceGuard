package app.priceguard.data.repository

import app.priceguard.ui.data.PricePatchResult
import app.priceguard.ui.data.ProductAddResult
import app.priceguard.ui.data.ProductData
import app.priceguard.ui.data.ProductDetailResult
import app.priceguard.ui.data.ProductVerifyResult
import app.priceguard.ui.data.RecommendProductData

interface ProductRepository {

    suspend fun verifyLink(productUrl: String, isRenewed: Boolean = false): ProductRepositoryResult<ProductVerifyResult>

    suspend fun addProduct(productCode: String, targetPrice: Int, isRenewed: Boolean = false): ProductRepositoryResult<ProductAddResult>

    suspend fun getProductList(isRenewed: Boolean = false): ProductRepositoryResult<List<ProductData>>

    suspend fun getRecommendedProductList(isRenewed: Boolean = false): ProductRepositoryResult<List<RecommendProductData>>

    suspend fun getProductDetail(productCode: String, isRenewed: Boolean = false): ProductRepositoryResult<ProductDetailResult>

    suspend fun deleteProduct(productCode: String, isRenewed: Boolean = false): ProductRepositoryResult<Boolean>

    suspend fun updateTargetPrice(productCode: String, targetPrice: Int, isRenewed: Boolean = false): ProductRepositoryResult<PricePatchResult>
}

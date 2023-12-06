package app.priceguard.data.repository

import app.priceguard.ui.data.PricePatchResult
import app.priceguard.ui.data.ProductAddResult
import app.priceguard.ui.data.ProductData
import app.priceguard.ui.data.ProductDetailResult
import app.priceguard.ui.data.ProductVerifyResult
import app.priceguard.ui.data.RecommendProductData

interface ProductRepository {

    suspend fun verifyLink(productUrl: String, isRenewed: Boolean = false): RepositoryResult<ProductVerifyResult, ProductErrorState>

    suspend fun addProduct(productCode: String, targetPrice: Int, isRenewed: Boolean = false): RepositoryResult<ProductAddResult, ProductErrorState>

    suspend fun getProductList(isRenewed: Boolean = false): RepositoryResult<List<ProductData>, ProductErrorState>

    suspend fun getRecommendedProductList(isRenewed: Boolean = false): RepositoryResult<List<RecommendProductData>, ProductErrorState>

    suspend fun getProductDetail(productCode: String, isRenewed: Boolean = false): RepositoryResult<ProductDetailResult, ProductErrorState>

    suspend fun deleteProduct(productCode: String, isRenewed: Boolean = false): RepositoryResult<Boolean, ProductErrorState>

    suspend fun updateTargetPrice(productCode: String, targetPrice: Int, isRenewed: Boolean = false): RepositoryResult<PricePatchResult, ProductErrorState>
}

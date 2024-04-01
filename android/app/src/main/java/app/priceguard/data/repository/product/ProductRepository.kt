package app.priceguard.data.repository.product

import app.priceguard.data.repository.RepositoryResult
import app.priceguard.ui.data.PricePatchResult
import app.priceguard.ui.data.ProductAddResult
import app.priceguard.ui.data.ProductData
import app.priceguard.ui.data.ProductDetailResult
import app.priceguard.ui.data.ProductVerifyResult
import app.priceguard.ui.data.RecommendProductData

interface ProductRepository {

    suspend fun verifyLink(productUrl: String, isRenewed: Boolean = false): RepositoryResult<ProductVerifyResult, ProductErrorState>

    suspend fun addProduct(shop: String, productCode: String, targetPrice: Int, isRenewed: Boolean = false): RepositoryResult<ProductAddResult, ProductErrorState>

    suspend fun getProductList(isRenewed: Boolean = false): RepositoryResult<List<ProductData>, ProductErrorState>

    suspend fun getRecommendedProductList(isRenewed: Boolean = false): RepositoryResult<List<RecommendProductData>, ProductErrorState>

    suspend fun getProductDetail(shop: String, productCode: String, isRenewed: Boolean = false): RepositoryResult<ProductDetailResult, ProductErrorState>

    suspend fun deleteProduct(shop: String, productCode: String, isRenewed: Boolean = false): RepositoryResult<Boolean, ProductErrorState>

    suspend fun updateTargetPrice(shop: String, productCode: String, targetPrice: Int, isRenewed: Boolean = false): RepositoryResult<PricePatchResult, ProductErrorState>

    suspend fun switchAlert(shop: String, productCode: String, isRenewed: Boolean = false): RepositoryResult<Boolean, ProductErrorState>
}

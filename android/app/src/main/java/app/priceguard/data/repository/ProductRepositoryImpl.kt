package app.priceguard.data.repository

import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductListResponse
import app.priceguard.data.dto.ProductResponse
import app.priceguard.data.network.ProductAPI
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(private val productAPI: ProductAPI) : ProductRepository {

    override suspend fun verifyLink(productUrl: String): ProductResponse {
        TODO("Not yet implemented")
    }

    override suspend fun addProduct(productAddRequest: ProductAddRequest): ProductResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getProductList(): ProductListResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getRecommendedProductList(): ProductListResponse {
        TODO("Not yet implemented")
    }

    override suspend fun getProductDetail(productCode: String): ProductResponse {
        TODO("Not yet implemented")
    }

    override suspend fun deleteProduct(productCode: String): ProductResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateTargetPrice(productAddRequest: ProductAddRequest): ProductResponse {
        TODO("Not yet implemented")
    }
}

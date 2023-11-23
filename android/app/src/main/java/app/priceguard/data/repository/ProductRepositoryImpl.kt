package app.priceguard.data.repository

import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductListResponse
import app.priceguard.data.dto.ProductResponse
import app.priceguard.data.dto.ProductVerifyRequest
import app.priceguard.data.network.APIResult
import app.priceguard.data.network.ProductAPI
import app.priceguard.data.network.getApiResult
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(private val productAPI: ProductAPI) : ProductRepository {

    override suspend fun verifyLink(productUrl: ProductVerifyRequest): APIResult<ProductResponse> {
        val response = getApiResult {
            productAPI.verifyLink(productUrl)
        }
        when (response) {
            is APIResult.Success -> {
                return response
            }

            is APIResult.Error -> {
                return when (response.code) {
                    400 -> {
                        response
                    }

                    401 -> {
                        response
                    }

                    else -> {
                        response
                    }
                }
            }
        }
    }

    override suspend fun addProduct(productAddRequest: ProductAddRequest): APIResult<ProductResponse> {
        val response = getApiResult {
            productAPI.addProduct(productAddRequest)
        }
        when (response) {
            is APIResult.Success -> {
                return response
            }

            is APIResult.Error -> {
                return when (response.code) {
                    400 -> {
                        response
                    }

                    401 -> {
                        response
                    }

                    else -> {
                        response
                    }
                }
            }
        }
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

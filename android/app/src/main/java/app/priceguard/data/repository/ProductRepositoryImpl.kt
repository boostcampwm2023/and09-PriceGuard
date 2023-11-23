package app.priceguard.data.repository

import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductDetailResult
import app.priceguard.data.dto.ProductDetailState
import app.priceguard.data.dto.ProductListResponse
import app.priceguard.data.dto.ProductResponse
import app.priceguard.data.dto.ProductVerifyRequest
import app.priceguard.data.dto.RenewResult
import app.priceguard.data.network.APIResult
import app.priceguard.data.network.ProductAPI
import app.priceguard.data.network.getApiResult
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productAPI: ProductAPI,
    private val tokenRepository: TokenRepository
) : ProductRepository {

    override suspend fun verifyLink(productUrl: ProductVerifyRequest): ProductResponse {
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

    override suspend fun getProductDetail(
        productCode: String, renewed: Boolean
    ): ProductDetailResult {
        when (val response = getApiResult { productAPI.getProductDetail(productCode) }) {
            is APIResult.Success -> {
                return ProductDetailResult(
                    ProductDetailState.SUCCESS,
                    productName = response.data.productName ?: "",
                    productCode = response.data.productCode,
                    shop = response.data.shop,
                    imageUrl = response.data.imageUrl,
                    rank = response.data.rank,
                    shopUrl = response.data.shopUrl,
                    targetPrice = response.data.targetPrice,
                    lowestPrice = response.data.lowestPrice,
                    price = response.data.price
                )
            }

            is APIResult.Error -> {
                when (response.code) {
                    401 -> {
                        if (renewed) {
                            return ProductDetailResult(ProductDetailState.PERMISSION_DENIED)
                        }

                        val refreshToken =
                            tokenRepository.getRefreshToken() ?: return ProductDetailResult(
                                ProductDetailState.PERMISSION_DENIED
                            )

                        val renewResult = tokenRepository.renewTokens(refreshToken)

                        if (renewResult != RenewResult.SUCCESS) {
                            return ProductDetailResult(ProductDetailState.PERMISSION_DENIED)
                        }

                        return getProductDetail(productCode, true)
                    }

                    404 -> {
                        return ProductDetailResult(ProductDetailState.NOT_FOUND)
                    }

                    else -> {
                        return ProductDetailResult(ProductDetailState.UNDEFINED_ERROR)
                    }
                }
            }
        }
    }

    override suspend fun deleteProduct(productCode: String): ProductResponse {
        TODO("Not yet implemented")
    }

    override suspend fun updateTargetPrice(productAddRequest: ProductAddRequest): ProductResponse {
        TODO("Not yet implemented")
    }
}

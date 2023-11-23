package app.priceguard.data.repository

import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductData
import app.priceguard.data.dto.ProductListResult
import app.priceguard.data.dto.ProductListState
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
) :
    ProductRepository {

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

    override suspend fun getProductList(afterRenew: Boolean): ProductListResult {
        val response = getApiResult {
            productAPI.getProductList()
        }
        when (response) {
            is APIResult.Success -> {
                return ProductListResult(
                    ProductListState.SUCCESS,
                    response.data.trackingList?.map { dto ->
                        ProductData(
                            dto.productName ?: "",
                            dto.productCode ?: "",
                            dto.shop ?: "",
                            dto.imageUrl ?: ""
                        )
                    } ?: listOf()
                )
            }

            is APIResult.Error -> {
                when (response.code) {
                    401 -> {
                        if (afterRenew) {
                            return ProductListResult(ProductListState.PERMISSION_DENIED, listOf())
                        } else {
                            val refreshToken =
                                tokenRepository.getRefreshToken() ?: return ProductListResult(
                                    ProductListState.PERMISSION_DENIED,
                                    listOf()
                                )
                            val renewResult = tokenRepository.renewTokens(refreshToken)
                            if (renewResult != RenewResult.SUCCESS) {
                                return ProductListResult(ProductListState.PERMISSION_DENIED, listOf())
                            }
                            return getProductList(afterRenew = true)
                        }
                    }

                    404 -> {
                        return ProductListResult(ProductListState.NOT_FOUND, listOf())
                    }

                    else -> {
                        return ProductListResult(ProductListState.UNDEFINED_ERROR, listOf())
                    }
                }
            }
        }
    }

    override suspend fun getRecommendedProductList(afterRenew: Boolean): ProductListResult {
        val response = getApiResult {
            productAPI.getRecommendedProductList()
        }
        when (response) {
            is APIResult.Success -> {
                return ProductListResult(
                    ProductListState.SUCCESS,
                    response.data.trackingList?.map { dto ->
                        ProductData(
                            dto.productName ?: "",
                            dto.productCode ?: "",
                            dto.shop ?: "",
                            dto.imageUrl ?: ""
                        )
                    } ?: listOf()
                )
            }

            is APIResult.Error -> {
                return when (response.code) {
                    401 -> {
                        if (afterRenew) {
                            return ProductListResult(ProductListState.PERMISSION_DENIED, listOf())
                        } else {
                            val refreshToken =
                                tokenRepository.getRefreshToken() ?: return ProductListResult(
                                    ProductListState.PERMISSION_DENIED,
                                    listOf()
                                )
                            val renewResult = tokenRepository.renewTokens(refreshToken)
                            if (renewResult != RenewResult.SUCCESS) {
                                return ProductListResult(ProductListState.PERMISSION_DENIED, listOf())
                            }
                            return getRecommendedProductList(afterRenew = true)
                        }
                    }

                    404 -> {
                        ProductListResult(ProductListState.NOT_FOUND, listOf())
                    }

                    else -> {
                        ProductListResult(ProductListState.UNDEFINED_ERROR, listOf())
                    }
                }
            }
        }
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

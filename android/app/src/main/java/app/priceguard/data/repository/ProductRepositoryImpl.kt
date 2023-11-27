package app.priceguard.data.repository

import app.priceguard.data.dto.ErrorState
import app.priceguard.data.dto.PricePatchRequest
import app.priceguard.data.dto.PricePatchResponse
import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductAddResponse
import app.priceguard.data.dto.ProductData
import app.priceguard.data.dto.ProductDeleteState
import app.priceguard.data.dto.ProductDetailResult
import app.priceguard.data.dto.ProductDetailState
import app.priceguard.data.dto.ProductListResult
import app.priceguard.data.dto.ProductListState
import app.priceguard.data.dto.ProductVerifyDTO
import app.priceguard.data.dto.ProductVerifyRequest
import app.priceguard.data.dto.RecommendProductData
import app.priceguard.data.dto.RecommendProductResult
import app.priceguard.data.dto.RecommendProductState
import app.priceguard.data.dto.RenewResult
import app.priceguard.data.network.APIResult
import app.priceguard.data.network.ProductAPI
import app.priceguard.data.network.RepositoryResult
import app.priceguard.data.network.getApiResult
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productAPI: ProductAPI,
    private val tokenRepository: TokenRepository
) : ProductRepository {

    private suspend fun <T> renew(apiFunc: RepositoryResult<T>): RepositoryResult<T> {
        val refreshToken =
            tokenRepository.getRefreshToken()
                ?: return RepositoryResult.Error(ErrorState.TOKEN_ERROR)

        val renewResult = tokenRepository.renewTokens(refreshToken)

        if (renewResult != RenewResult.SUCCESS) {
            return RepositoryResult.Error(ErrorState.TOKEN_ERROR)
        }
        return apiFunc
    }

    override suspend fun verifyLink(
        productUrl: ProductVerifyRequest,
        isRenewed: Boolean
    ): RepositoryResult<ProductVerifyDTO> {
        val response = getApiResult {
            productAPI.verifyLink(productUrl)
        }
        when (response) {
            is APIResult.Success -> {
                return RepositoryResult.Success(
                    ProductVerifyDTO(
                        response.data.productName,
                        response.data.productCode,
                        response.data.productPrice,
                        response.data.shop,
                        response.data.imageUrl
                    )
                )
            }

            is APIResult.Error -> {
                return when (response.code) {
                    400 -> {
                        RepositoryResult.Error(ErrorState.INVALID_REQUEST)
                    }

                    401 -> {
                        RepositoryResult.Error(ErrorState.PERMISSION_DENIED)
                    }

                    410 -> {
                        if (!isRenewed) {
                            renew(verifyLink(productUrl, true))
                        } else {
                            RepositoryResult.Error(ErrorState.TOKEN_ERROR)
                        }
                    }

                    else -> {
                        RepositoryResult.Error(ErrorState.UNDEFINED_ERROR)
                    }
                }
            }
        }
    }

    override suspend fun addProduct(
        productAddRequest: ProductAddRequest,
        isRenewed: Boolean
    ): RepositoryResult<ProductAddResponse> {
        val response = getApiResult {
            productAPI.addProduct(productAddRequest)
        }
        when (response) {
            is APIResult.Success -> {
                return RepositoryResult.Success(
                    ProductAddResponse(
                        response.data.statusCode,
                        response.data.message
                    )
                )
            }

            is APIResult.Error -> {
                return when (response.code) {
                    400 -> {
                        RepositoryResult.Error(ErrorState.INVALID_REQUEST)
                    }

                    401 -> {
                        RepositoryResult.Error(ErrorState.PERMISSION_DENIED)
                    }

                    404 -> {
                        RepositoryResult.Error(ErrorState.NOT_FOUND)
                    }

                    409 -> {
                        RepositoryResult.Error(ErrorState.EXIST)
                    }

                    410 -> {
                        if (!isRenewed) {
                            renew(addProduct(productAddRequest, true))
                        } else {
                            RepositoryResult.Error(ErrorState.TOKEN_ERROR)
                        }
                    }

                    else -> {
                        RepositoryResult.Error(ErrorState.UNDEFINED_ERROR)
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
                            dto.imageUrl ?: "",
                            dto.targetPrice ?: 0,
                            dto.price ?: 0
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
                                return ProductListResult(
                                    ProductListState.PERMISSION_DENIED,
                                    listOf()
                                )
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

    override suspend fun getRecommendedProductList(afterRenew: Boolean): RecommendProductResult {
        val response = getApiResult {
            productAPI.getRecommendedProductList()
        }
        when (response) {
            is APIResult.Success -> {
                return RecommendProductResult(
                    RecommendProductState.SUCCESS,
                    response.data.recommendList?.map { dto ->
                        RecommendProductData(
                            dto.productName ?: "",
                            dto.productCode ?: "",
                            dto.shop ?: "",
                            dto.imageUrl ?: "",
                            dto.price ?: 0,
                            dto.rank ?: 0
                        )
                    } ?: listOf()
                )
            }

            is APIResult.Error -> {
                return when (response.code) {
                    400 -> {
                        RecommendProductResult(RecommendProductState.WRONG_REQUEST, listOf())
                    }

                    401 -> {
                        if (afterRenew) {
                            return RecommendProductResult(
                                RecommendProductState.PERMISSION_DENIED,
                                listOf()
                            )
                        } else {
                            val refreshToken =
                                tokenRepository.getRefreshToken() ?: return RecommendProductResult(
                                    RecommendProductState.PERMISSION_DENIED,
                                    listOf()
                                )
                            val renewResult = tokenRepository.renewTokens(refreshToken)
                            if (renewResult != RenewResult.SUCCESS) {
                                return RecommendProductResult(
                                    RecommendProductState.PERMISSION_DENIED,
                                    listOf()
                                )
                            }
                            return getRecommendedProductList(afterRenew = true)
                        }
                    }

                    404 -> {
                        RecommendProductResult(RecommendProductState.NOT_FOUND, listOf())
                    }

                    else -> {
                        RecommendProductResult(RecommendProductState.UNDEFINED_ERROR, listOf())
                    }
                }
            }
        }
    }

    override suspend fun getProductDetail(
        productCode: String,
        renewed: Boolean
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

    override suspend fun deleteProduct(productCode: String, renewed: Boolean): ProductDeleteState {
        when (val response = getApiResult { productAPI.deleteProduct(productCode) }) {
            is APIResult.Success -> {
                return ProductDeleteState.SUCCESS
            }

            is APIResult.Error -> {
                when (response.code) {
                    400 -> {
                        return ProductDeleteState.INVALID_REQUEST
                    }

                    401 -> {
                        if (renewed) {
                            return ProductDeleteState.UNAUTHORIZED
                        }

                        val refreshToken = tokenRepository.getRefreshToken()
                            ?: return ProductDeleteState.UNAUTHORIZED
                        val renewResult = tokenRepository.renewTokens(refreshToken)

                        if (renewResult != RenewResult.SUCCESS) {
                            return ProductDeleteState.UNAUTHORIZED
                        }

                        return deleteProduct(productCode, true)
                    }

                    404 -> {
                        return ProductDeleteState.NOT_FOUND
                    }

                    else -> {
                        return ProductDeleteState.UNDEFINED_ERROR
                    }
                }
            }
        }
    }

    override suspend fun updateTargetPrice(pricePatchRequest: PricePatchRequest): APIResult<PricePatchResponse> {
        val response = getApiResult {
            productAPI.updateTargetPrice(pricePatchRequest)
        }
        return response
    }
}

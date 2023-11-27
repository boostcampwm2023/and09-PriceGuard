package app.priceguard.data.repository

import app.priceguard.data.dto.ErrorState
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

    private suspend fun <T> renew(repoFun: suspend () -> RepositoryResult<T>): RepositoryResult<T> {
        val refreshToken =
            tokenRepository.getRefreshToken()
                ?: return RepositoryResult.Error(ErrorState.TOKEN_ERROR)

        val renewResult = tokenRepository.renewTokens(refreshToken)

        if (renewResult != RenewResult.SUCCESS) {
            return RepositoryResult.Error(ErrorState.TOKEN_ERROR)
        }
        return repoFun.invoke()
    }

    private suspend fun <T> handleError(
        code: Int?,
        repoFun: suspend () -> RepositoryResult<T>
    ): RepositoryResult<T> {
        return when (code) {
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
                renew(repoFun)
            }

            else -> {
                RepositoryResult.Error(ErrorState.UNDEFINED_ERROR)
            }
        }
    }

    override suspend fun verifyLink(
        productUrl: ProductVerifyRequest,
        isRenewed: Boolean
    ): RepositoryResult<ProductVerifyDTO> {
        if (isRenewed) {
            return RepositoryResult.Error(ErrorState.TOKEN_ERROR)
        }
        val response = getApiResult {
            productAPI.verifyLink(productUrl)
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
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
                handleError(response.code) {
                    verifyLink(productUrl, true)
                }
            }
        }
    }

    override suspend fun addProduct(
        productAddRequest: ProductAddRequest,
        isRenewed: Boolean
    ): RepositoryResult<ProductAddResponse> {
        if (isRenewed) {
            return RepositoryResult.Error(ErrorState.TOKEN_ERROR)
        }
        val response = getApiResult {
            productAPI.addProduct(productAddRequest)
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
                    ProductAddResponse(
                        response.data.statusCode,
                        response.data.message
                    )
                )
            }

            is APIResult.Error -> {
                handleError(response.code) {
                    addProduct(productAddRequest, true)
                }
            }
        }
    }

    override suspend fun getProductList(isRenewed: Boolean): RepositoryResult<List<ProductData>> {
        if (isRenewed) {
            return RepositoryResult.Error(ErrorState.TOKEN_ERROR)
        }
        val response = getApiResult {
            productAPI.getProductList()
        }
        when (response) {
            is APIResult.Success -> {
                return RepositoryResult.Success(
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
                return handleError(response.code) {
                    getProductList(true)
                }
            }
        }
    }

    override suspend fun getRecommendedProductList(isRenewed: Boolean): RepositoryResult<List<RecommendProductData>> {
        if (isRenewed) {
            return RepositoryResult.Error(ErrorState.TOKEN_ERROR)
        }
        val response = getApiResult {
            productAPI.getRecommendedProductList()
        }
        when (response) {
            is APIResult.Success -> {
                return RepositoryResult.Success(
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
                return handleError(response.code) {
                    getRecommendedProductList(true)
                }
            }
        }
    }

    override suspend fun getProductDetail(
        productCode: String,
        isRenewed: Boolean
    ): RepositoryResult<ProductDetailResult> {
        if (isRenewed) {
            return RepositoryResult.Error(ErrorState.TOKEN_ERROR)
        }
        val response = getApiResult {
            productAPI.getProductDetail(productCode)
        }
        when (response) {
            is APIResult.Success -> {
                return RepositoryResult.Success(
                    ProductDetailResult(
                        productName = response.data.productName ?: "",
                        productCode = response.data.productCode ?: "",
                        shop = response.data.shop ?: "",
                        imageUrl = response.data.imageUrl ?: "",
                        rank = response.data.rank ?: -1,
                        shopUrl = response.data.shopUrl ?: "",
                        targetPrice = response.data.targetPrice ?: -1,
                        lowestPrice = response.data.lowestPrice ?: -1,
                        price = response.data.price ?: -1
                    )
                )
            }

            is APIResult.Error -> {
                return handleError(response.code) {
                    getProductDetail(productCode, true)
                }
            }
        }
    }

    override suspend fun deleteProduct(
        productCode: String,
        isRenewed: Boolean
    ): ProductDeleteState {
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
                        if (isRenewed) {
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

    override suspend fun updateTargetPrice(
        pricePatchRequest: PricePatchRequest,
        isRenewed: Boolean
    ): RepositoryResult<PricePatchResponse> {
        if (isRenewed) {
            return RepositoryResult.Error(ErrorState.TOKEN_ERROR)
        }
        val response = getApiResult {
            productAPI.updateTargetPrice(pricePatchRequest)
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
                    PricePatchResponse(
                        response.data.statusCode,
                        response.data.message
                    )
                )
            }

            is APIResult.Error -> {
                handleError(response.code) {
                    updateTargetPrice(pricePatchRequest, false)
                }
            }
        }
    }
}

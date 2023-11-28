package app.priceguard.data.repository

import app.priceguard.data.dto.PricePatchRequest
import app.priceguard.data.dto.PricePatchResponse
import app.priceguard.data.dto.ProductAddRequest
import app.priceguard.data.dto.ProductAddResponse
import app.priceguard.data.dto.ProductData
import app.priceguard.data.dto.ProductDetailResult
import app.priceguard.data.dto.ProductErrorState
import app.priceguard.data.dto.ProductVerifyDTO
import app.priceguard.data.dto.ProductVerifyRequest
import app.priceguard.data.dto.RecommendProductData
import app.priceguard.data.dto.RenewResult
import app.priceguard.data.network.APIResult
import app.priceguard.data.network.ProductAPI
import app.priceguard.data.network.ProductRepositoryResult
import app.priceguard.data.network.getApiResult
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productAPI: ProductAPI,
    private val tokenRepository: TokenRepository
) : ProductRepository {

    private suspend fun renew(): Boolean {
        val refreshToken = tokenRepository.getRefreshToken() ?: return false
        val renewResult = tokenRepository.renewTokens(refreshToken)
        if (renewResult != RenewResult.SUCCESS) {
            return false
        }
        return true
    }

    private suspend fun <T> handleError(
        code: Int?,
        isRenewed: Boolean,
        repoFun: suspend () -> ProductRepositoryResult<T>
    ): ProductRepositoryResult<T> {
        return when (code) {
            400 -> {
                ProductRepositoryResult.Error(ProductErrorState.INVALID_REQUEST)
            }

            401 -> {
                ProductRepositoryResult.Error(ProductErrorState.PERMISSION_DENIED)
            }

            404 -> {
                ProductRepositoryResult.Error(ProductErrorState.NOT_FOUND)
            }

            409 -> {
                ProductRepositoryResult.Error(ProductErrorState.EXIST)
            }

            410 -> {
                if (isRenewed) {
                    ProductRepositoryResult.Error(ProductErrorState.PERMISSION_DENIED)
                } else {
                    if (renew()) {
                        repoFun.invoke()
                    } else {
                        ProductRepositoryResult.Error(ProductErrorState.PERMISSION_DENIED)
                    }
                }
            }

            else -> {
                ProductRepositoryResult.Error(ProductErrorState.UNDEFINED_ERROR)
            }
        }
    }

    override suspend fun verifyLink(
        productUrl: ProductVerifyRequest,
        isRenewed: Boolean
    ): ProductRepositoryResult<ProductVerifyDTO> {
        val response = getApiResult {
            productAPI.verifyLink(productUrl)
        }
        return when (response) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(
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
                handleError(response.code, isRenewed) {
                    verifyLink(productUrl, true)
                }
            }
        }
    }

    override suspend fun addProduct(
        productAddRequest: ProductAddRequest,
        isRenewed: Boolean
    ): ProductRepositoryResult<ProductAddResponse> {
        val response = getApiResult {
            productAPI.addProduct(productAddRequest)
        }
        return when (response) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(
                    ProductAddResponse(
                        response.data.statusCode,
                        response.data.message
                    )
                )
            }

            is APIResult.Error -> {
                handleError(response.code, isRenewed) {
                    addProduct(productAddRequest, true)
                }
            }
        }
    }

    override suspend fun getProductList(isRenewed: Boolean): ProductRepositoryResult<List<ProductData>> {
        val response = getApiResult {
            productAPI.getProductList()
        }
        return when (response) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(
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
                handleError(response.code, isRenewed) {
                    getProductList(true)
                }
            }
        }
    }

    override suspend fun getRecommendedProductList(isRenewed: Boolean): ProductRepositoryResult<List<RecommendProductData>> {
        val response = getApiResult {
            productAPI.getRecommendedProductList()
        }
        return when (response) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(
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
                handleError(response.code, isRenewed) {
                    getRecommendedProductList(true)
                }
            }
        }
    }

    override suspend fun getProductDetail(
        productCode: String,
        isRenewed: Boolean
    ): ProductRepositoryResult<ProductDetailResult> {
        val response = getApiResult {
            productAPI.getProductDetail(productCode)
        }
        return when (response) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(
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
                handleError(response.code, isRenewed) {
                    getProductDetail(productCode, true)
                }
            }
        }
    }

    override suspend fun deleteProduct(
        productCode: String,
        isRenewed: Boolean
    ): ProductRepositoryResult<Boolean> {
        return when (val response = getApiResult { productAPI.deleteProduct(productCode) }) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(true)
            }

            is APIResult.Error -> {
                handleError(response.code, isRenewed) {
                    deleteProduct(productCode, true)
                }
            }
        }
    }

    override suspend fun updateTargetPrice(
        pricePatchRequest: PricePatchRequest,
        isRenewed: Boolean
    ): ProductRepositoryResult<PricePatchResponse> {
        val response = getApiResult {
            productAPI.updateTargetPrice(pricePatchRequest)
        }
        return when (response) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(
                    PricePatchResponse(
                        response.data.statusCode,
                        response.data.message
                    )
                )
            }

            is APIResult.Error -> {
                handleError(response.code, isRenewed) {
                    updateTargetPrice(pricePatchRequest, true)
                }
            }
        }
    }
}

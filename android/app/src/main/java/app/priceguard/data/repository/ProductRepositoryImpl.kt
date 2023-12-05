package app.priceguard.data.repository

import app.priceguard.data.GraphDataConverter
import app.priceguard.data.dto.add.ProductAddRequest
import app.priceguard.data.dto.add.ProductAddResult
import app.priceguard.data.dto.detail.ProductDetailResult
import app.priceguard.data.dto.list.ProductData
import app.priceguard.data.dto.patch.PricePatchRequest
import app.priceguard.data.dto.patch.PricePatchResult
import app.priceguard.data.dto.recommend.RecommendProductData
import app.priceguard.data.dto.renew.RenewResult
import app.priceguard.data.dto.verify.ProductVerifyRequest
import app.priceguard.data.dto.verify.ProductVerifyResult
import app.priceguard.data.network.APIResult
import app.priceguard.data.network.ProductAPI
import app.priceguard.data.network.ProductErrorState
import app.priceguard.data.network.ProductRepositoryResult
import app.priceguard.data.network.getApiResult
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productAPI: ProductAPI,
    private val tokenRepository: TokenRepository,
    private val graphDataConverter: GraphDataConverter
) : ProductRepository {

    private suspend fun renew(): Boolean {
        val refreshToken = tokenRepository.getRefreshToken() ?: return false
        val renewResult = tokenRepository.renewTokens(refreshToken)
        return renewResult == RenewResult.SUCCESS
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
    ): ProductRepositoryResult<ProductVerifyResult> {
        val response = getApiResult {
            productAPI.verifyLink(productUrl)
        }
        return when (response) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(
                    ProductVerifyResult(
                        response.data.productName ?: "",
                        response.data.productCode ?: "",
                        response.data.productPrice ?: 0,
                        response.data.shop ?: "",
                        response.data.imageUrl ?: ""
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
    ): ProductRepositoryResult<ProductAddResult> {
        val response = getApiResult {
            productAPI.addProduct(productAddRequest)
        }
        return when (response) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(
                    ProductAddResult(
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
                            dto.price ?: 0,
                            GraphDataConverter().toDataset(dto.priceData)
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
                            dto.rank ?: 0,
                            GraphDataConverter().toDataset(dto.priceData)
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
                        price = response.data.price ?: -1,
                        priceData = graphDataConverter.toDataset(response.data.priceData)
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
    ): ProductRepositoryResult<PricePatchResult> {
        val response = getApiResult {
            productAPI.updateTargetPrice(pricePatchRequest)
        }
        return when (response) {
            is APIResult.Success -> {
                ProductRepositoryResult.Success(
                    PricePatchResult(
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

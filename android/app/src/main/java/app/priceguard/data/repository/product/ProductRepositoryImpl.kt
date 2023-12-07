package app.priceguard.data.repository.product

import app.priceguard.data.GraphDataConverter
import app.priceguard.data.dto.add.ProductAddRequest
import app.priceguard.data.dto.patch.PricePatchRequest
import app.priceguard.data.dto.verify.ProductVerifyRequest
import app.priceguard.data.network.ProductAPI
import app.priceguard.data.repository.APIResult
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.getApiResult
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.ui.data.PricePatchResult
import app.priceguard.ui.data.ProductAddResult
import app.priceguard.ui.data.ProductData
import app.priceguard.ui.data.ProductDetailResult
import app.priceguard.ui.data.ProductVerifyResult
import app.priceguard.ui.data.RecommendProductData
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productAPI: ProductAPI,
    private val tokenRepository: TokenRepository,
    private val graphDataConverter: GraphDataConverter
) : ProductRepository {

    private suspend fun renew(): Boolean {
        val refreshToken = tokenRepository.getRefreshToken() ?: return false
        return when (tokenRepository.renewTokens(refreshToken)) {
            is RepositoryResult.Success -> {
                true
            }

            is RepositoryResult.Error -> {
                false
            }
        }
    }

    private suspend fun <T> handleError(
        code: Int?,
        isRenewed: Boolean,
        repoFun: suspend () -> RepositoryResult<T, ProductErrorState>
    ): RepositoryResult<T, ProductErrorState> {
        return when (code) {
            400 -> {
                RepositoryResult.Error(ProductErrorState.INVALID_REQUEST)
            }

            401 -> {
                RepositoryResult.Error(ProductErrorState.PERMISSION_DENIED)
            }

            404 -> {
                RepositoryResult.Error(ProductErrorState.NOT_FOUND)
            }

            409 -> {
                RepositoryResult.Error(ProductErrorState.EXIST)
            }

            410 -> {
                if (isRenewed) {
                    RepositoryResult.Error(ProductErrorState.PERMISSION_DENIED)
                } else {
                    if (renew()) {
                        repoFun.invoke()
                    } else {
                        RepositoryResult.Error(ProductErrorState.PERMISSION_DENIED)
                    }
                }
            }

            else -> {
                RepositoryResult.Error(ProductErrorState.UNDEFINED_ERROR)
            }
        }
    }

    override suspend fun verifyLink(
        productUrl: String,
        isRenewed: Boolean
    ): RepositoryResult<ProductVerifyResult, ProductErrorState> {
        val response = getApiResult {
            productAPI.verifyLink(ProductVerifyRequest(productUrl))
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
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
        productCode: String,
        targetPrice: Int,
        isRenewed: Boolean
    ): RepositoryResult<ProductAddResult, ProductErrorState> {
        val response = getApiResult {
            productAPI.addProduct(ProductAddRequest(productCode, targetPrice))
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
                    ProductAddResult(
                        response.data.statusCode,
                        response.data.message
                    )
                )
            }

            is APIResult.Error -> {
                handleError(response.code, isRenewed) {
                    addProduct(productCode, targetPrice, true)
                }
            }
        }
    }

    override suspend fun getProductList(isRenewed: Boolean): RepositoryResult<List<ProductData>, ProductErrorState> {
        val response = getApiResult {
            productAPI.getProductList()
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
                    response.data.trackingList?.map { dto ->
                        ProductData(
                            dto.productName ?: "",
                            dto.productCode ?: "",
                            dto.shop ?: "",
                            dto.imageUrl ?: "",
                            dto.targetPrice ?: 0,
                            dto.price ?: 0,
                            dto.isAlert ?: true,
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

    override suspend fun getRecommendedProductList(isRenewed: Boolean): RepositoryResult<List<RecommendProductData>, ProductErrorState> {
        val response = getApiResult {
            productAPI.getRecommendedProductList()
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
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
    ): RepositoryResult<ProductDetailResult, ProductErrorState> {
        val response = getApiResult {
            productAPI.getProductDetail(productCode)
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
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
    ): RepositoryResult<Boolean, ProductErrorState> {
        return when (val response = getApiResult { productAPI.deleteProduct(productCode) }) {
            is APIResult.Success -> {
                RepositoryResult.Success(true)
            }

            is APIResult.Error -> {
                handleError(response.code, isRenewed) {
                    deleteProduct(productCode, true)
                }
            }
        }
    }

    override suspend fun updateTargetPrice(
        productCode: String,
        targetPrice: Int,
        isRenewed: Boolean
    ): RepositoryResult<PricePatchResult, ProductErrorState> {
        val response = getApiResult {
            productAPI.updateTargetPrice(PricePatchRequest(productCode, targetPrice))
        }
        return when (response) {
            is APIResult.Success -> {
                RepositoryResult.Success(
                    PricePatchResult(
                        response.data.statusCode,
                        response.data.message
                    )
                )
            }

            is APIResult.Error -> {
                handleError(response.code, isRenewed) {
                    updateTargetPrice(productCode, targetPrice, true)
                }
            }
        }
    }

    override suspend fun switchAlert(productCode: String, isRenewed: Boolean): RepositoryResult<Boolean, ProductErrorState> {
        return when (val response = getApiResult { productAPI.updateAlert(productCode) }) {
            is APIResult.Success -> {
                RepositoryResult.Success(true)
            }

            is APIResult.Error -> {
                handleError(response.code, isRenewed) {
                    deleteProduct(productCode, true)
                }
            }
        }
    }
}

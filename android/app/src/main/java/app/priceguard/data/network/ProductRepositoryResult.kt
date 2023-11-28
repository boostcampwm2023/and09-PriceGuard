package app.priceguard.data.network

import app.priceguard.data.dto.ProductErrorState

sealed class ProductRepositoryResult<out T> {

    data class Success<out T>(val data: T) : ProductRepositoryResult<T>()

    data class Error(val productErrorState: ProductErrorState) : ProductRepositoryResult<Nothing>()
}

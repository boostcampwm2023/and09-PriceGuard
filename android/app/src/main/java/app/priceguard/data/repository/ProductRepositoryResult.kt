package app.priceguard.data.repository

sealed class ProductRepositoryResult<out T> {

    data class Success<out T>(val data: T) : ProductRepositoryResult<T>()

    data class Error(val productErrorState: ProductErrorState) : ProductRepositoryResult<Nothing>()
}

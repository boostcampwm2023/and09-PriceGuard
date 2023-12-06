package app.priceguard.data.repository

sealed class RepositoryResult<out T, out R> {

    data class Success<out T>(val data: T) : RepositoryResult<T, Nothing>()

    data class Error<out R>(val errorState: R) : RepositoryResult<Nothing, R>()
}

package app.priceguard.data.repository

sealed class RepositoryResult<out T, out S> {

    data class Success<out T>(val data: T) : RepositoryResult<T, Nothing>()

    data class Error<out S>(val errorState: S) : RepositoryResult<Nothing, S>()
}

package app.priceguard.data.repository

sealed class TokenRepositoryResult<out T> {

    data class Success<out T>(val data: T) : TokenRepositoryResult<T>()

    data class Error(val tokenErrorState: TokenErrorState) : TokenRepositoryResult<Nothing>()
}

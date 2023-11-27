package app.priceguard.data.network

import app.priceguard.data.dto.ErrorState

sealed class RepositoryResult<out T> {

    data class Success<out T>(val data: T) : RepositoryResult<T>()

    data class Error(val errorState: ErrorState) : RepositoryResult<Nothing>()
}

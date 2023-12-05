package app.priceguard.data.network

sealed class AuthRepositoryResult<out T> {

    data class Success<out T>(val data: T) : AuthRepositoryResult<T>()

    data class Error(val authErrorState: AuthErrorState) : AuthRepositoryResult<Nothing>()
}

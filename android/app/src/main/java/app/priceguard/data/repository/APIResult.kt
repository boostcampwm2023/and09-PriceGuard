package app.priceguard.data.repository

import retrofit2.Response

sealed class APIResult<out T> {

    data class Success<out T>(val data: T) : APIResult<T>()

    data class Error(val code: Int? = null, val exception: Throwable? = null) : APIResult<Nothing>()
}

suspend fun <T> getApiResult(apiFunc: suspend () -> Response<T>): APIResult<T> {
    try {
        val response = apiFunc.invoke()
        if (response.isSuccessful) {
            response.body()?.let {
                return APIResult.Success(it)
            }
        }
        return APIResult.Error(code = response.code())
    } catch (e: Exception) {
        return APIResult.Error(exception = e)
    }
}

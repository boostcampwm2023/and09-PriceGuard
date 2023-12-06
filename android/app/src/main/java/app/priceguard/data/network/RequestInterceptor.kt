package app.priceguard.data.network

import app.priceguard.data.repository.token.TokenRepository
import javax.inject.Inject
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor @Inject constructor(private val tokenRepository: TokenRepository) :
    Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()

        runBlocking {
            builder.addHeader("Authorization", "Bearer ${tokenRepository.getAccessToken()}")
        }

        return chain.proceed(builder.build())
    }
}

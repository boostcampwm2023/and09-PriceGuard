package app.priceguard.di

import app.priceguard.data.network.ProductAPI
import app.priceguard.data.network.UserAPI
import app.priceguard.data.repository.TokenRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://api.priceguard.app/"
    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideUserAPI(): UserAPI = Retrofit.Builder()
        .baseUrl("${BASE_URL}user/")
        .addConverterFactory(json.asConverterFactory(MediaType.parse("application/json")!!))
        .build()
        .create(UserAPI::class.java)

    @Provides
    @Singleton
    fun provideProduct(requestInterceptor: RequestInterceptor): ProductAPI {
        val interceptorClient = OkHttpClient().newBuilder()
            .addInterceptor(requestInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory(MediaType.parse("application/json")!!))
            .client(interceptorClient)
            .build()
            .create(ProductAPI::class.java)
    }
}

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

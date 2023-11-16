package app.priceguard.data.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit

class RetrofitBuilder {
    private val baseUrl = "https://api.priceguard.app"
    private val json = Json { ignoreUnknownKeys = true }
    fun getUserRetrofit(): UserAPI {
        return Retrofit.Builder()
            .baseUrl("$baseUrl/user")
            .addConverterFactory(json.asConverterFactory(MediaType.parse("application/json")!!))
            .build()
            .create(UserAPI::class.java)
    }
}

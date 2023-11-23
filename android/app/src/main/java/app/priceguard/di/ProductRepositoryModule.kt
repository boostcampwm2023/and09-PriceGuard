package app.priceguard.di

import app.priceguard.data.network.ProductAPI
import app.priceguard.data.repository.ProductRepository
import app.priceguard.data.repository.ProductRepositoryImpl
import app.priceguard.data.repository.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProductRepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(productAPI: ProductAPI, tokenRepository: TokenRepository): ProductRepository =
        ProductRepositoryImpl(productAPI, tokenRepository)
}

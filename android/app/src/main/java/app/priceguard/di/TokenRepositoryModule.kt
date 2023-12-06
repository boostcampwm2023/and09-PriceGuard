package app.priceguard.di

import app.priceguard.data.datastore.TokenDataSource
import app.priceguard.data.network.AuthAPI
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.data.repository.token.TokenRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TokenRepositoryModule {

    @Provides
    @Singleton
    fun provideTokenRepository(
        tokenDataSource: TokenDataSource,
        authAPI: AuthAPI
    ): TokenRepository =
        TokenRepositoryImpl(tokenDataSource, authAPI)
}

package app.priceguard.di

import app.priceguard.data.network.UserAPI
import app.priceguard.data.repository.AuthRepository
import app.priceguard.data.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthRepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userAPI: UserAPI): AuthRepository = AuthRepositoryImpl(userAPI)
}

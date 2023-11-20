package app.priceguard.di

import app.priceguard.data.network.UserAPI
import app.priceguard.data.repository.UserRepository
import app.priceguard.data.repository.UserRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserRepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(userAPI: UserAPI): UserRepository = UserRepositoryImpl(userAPI)
}

package app.priceguard.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.priceguard.data.datastore.TokenDataSource
import app.priceguard.data.datastore.TokenDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TokenDataSourceModule {

    @Provides
    @Singleton
    fun provideTokenDataSource(@TokensQualifier dataStore: DataStore<Preferences>): TokenDataSource =
        TokenDataSourceImpl(dataStore)
}

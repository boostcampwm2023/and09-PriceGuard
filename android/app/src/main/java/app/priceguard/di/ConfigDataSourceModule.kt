package app.priceguard.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import app.priceguard.data.datastore.ConfigDataSource
import app.priceguard.data.datastore.ConfigDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigDataSourceModule {

    @Provides
    @Singleton
    fun provideConfigDataSource(@ConfigQualifier dataStore: DataStore<Preferences>): ConfigDataSource =
        ConfigDataSourceImpl(dataStore)
}

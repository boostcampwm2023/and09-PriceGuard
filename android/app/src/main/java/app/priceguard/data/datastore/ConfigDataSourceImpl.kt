package app.priceguard.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import app.priceguard.di.ConfigQualifier
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ConfigDataSourceImpl @Inject constructor(
    @ConfigQualifier private val dataStore: DataStore<Preferences>
) : ConfigDataSource {

    private val dynamicMode = intPreferencesKey("dynamic_mode")
    private val darkMode = intPreferencesKey("dark_mode")

    override suspend fun saveDynamicMode(mode: Int) {
        dataStore.edit { preferences ->
            preferences[dynamicMode] = mode
        }
    }

    override suspend fun saveDarkMode(mode: Int) {
        dataStore.edit { preferences ->
            preferences[darkMode] = mode
        }
    }

    override suspend fun getDynamicMode(): Int? {
        return dataStore.data.map { preferences ->
            preferences[dynamicMode]
        }.first()
    }

    override suspend fun getDarkMode(): Int? {
        return dataStore.data.map { preferences ->
            preferences[darkMode]
        }.first()
    }
}

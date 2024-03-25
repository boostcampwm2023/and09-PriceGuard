package app.priceguard.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import app.priceguard.di.TokensQualifier
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TokenDataSourceImpl @Inject constructor(
    @TokensQualifier private val dataStore: DataStore<Preferences>
) : TokenDataSource {

    private val accessTokenKey = stringPreferencesKey("access_token")
    private val refreshTokenKey = stringPreferencesKey("refresh_token")
    private val isEmailVerifiedKey = booleanPreferencesKey("is_email_verified")

    override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.edit { preferences ->
            preferences[accessTokenKey] = accessToken
            preferences[refreshTokenKey] = refreshToken
        }
    }

    override suspend fun saveEmailVerified(isVerified: Boolean) {
        dataStore.edit { preferences ->
            preferences[isEmailVerifiedKey] = isVerified
        }
    }

    override suspend fun getAccessToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[accessTokenKey]
        }.first()
    }

    override suspend fun getRefreshToken(): String? {
        return dataStore.data.map { preferences ->
            preferences[refreshTokenKey]
        }.first()
    }

    override suspend fun getIsEmailVerified(): Boolean? {
        return dataStore.data.map { preferences ->
            preferences[isEmailVerifiedKey]
        }.first()
    }

    override suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}

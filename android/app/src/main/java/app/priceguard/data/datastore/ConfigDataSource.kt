package app.priceguard.data.datastore

interface ConfigDataSource {
    suspend fun saveDynamicMode(mode: Int)
    suspend fun saveDarkMode(mode: Int)
    suspend fun getDynamicMode(): Int?
    suspend fun getDarkMode(): Int?
}

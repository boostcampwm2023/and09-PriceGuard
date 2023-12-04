package app.priceguard.ui

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import app.priceguard.data.datastore.ConfigDataSource
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltAndroidApp
class PriceGuardApp : Application() {

    @Inject
    lateinit var configDataSource: ConfigDataSource

    override fun onCreate() {
        super.onCreate()

        initAppTheme()
    }

    private fun initAppTheme() {
        CoroutineScope(Dispatchers.IO).launch {
            val dynamicColorMode = configDataSource.getDynamicMode()
            val darkMode = configDataSource.getDarkMode()

            when (dynamicColorMode) {
                MODE_DYNAMIC -> {
                    DynamicColors.applyToActivitiesIfAvailable(this@PriceGuardApp)
                }
            }

            when (darkMode) {
                MODE_LIGHT -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        val uiModeManager =
                            getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                        uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_NO)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }

                MODE_DARK -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        val uiModeManager =
                            getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                        uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    }
                }
            }
        }
    }

    companion object {
        const val MODE_SYSTEM = 0
        const val MODE_LIGHT = 1
        const val MODE_DARK = 2

        const val MODE_DYNAMIC_NO = 0
        const val MODE_DYNAMIC = 1
    }
}

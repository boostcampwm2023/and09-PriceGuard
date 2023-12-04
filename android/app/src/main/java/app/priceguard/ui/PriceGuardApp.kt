package app.priceguard.ui

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import app.priceguard.data.datastore.ConfigDataSource
import app.priceguard.ui.home.theme.ThemeDialogFragment
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
                ThemeDialogFragment.MODE_DYNAMIC -> {
                    DynamicColors.applyToActivitiesIfAvailable(this@PriceGuardApp)
                }
            }

            when (darkMode) {
                ThemeDialogFragment.MODE_LIGHT -> {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                        val uiModeManager =
                            getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                        uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_NO)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    }
                }

                ThemeDialogFragment.MODE_DARK -> {
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
}

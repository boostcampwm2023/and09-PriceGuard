package app.priceguard.ui

import android.app.Application
import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import app.priceguard.ui.home.theme.ThemeDialogFragment
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PriceGuardApp : Application() {

    override fun onCreate() {
        super.onCreate()

        initAppTheme()
    }
    private fun initAppTheme() {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val dynamicColorMode = sharedPreferences.getInt("DynamicColor", ThemeDialogFragment.MODE_DYNAMIC_NO)
        val darkMode = sharedPreferences.getInt("DarkMode", ThemeDialogFragment.MODE_SYSTEM)

        when (dynamicColorMode) {
            ThemeDialogFragment.MODE_DYNAMIC -> {
                DynamicColors.applyToActivitiesIfAvailable(this)
            }
        }

        when (darkMode) {
            ThemeDialogFragment.MODE_LIGHT -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                    uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_NO)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }

            ThemeDialogFragment.MODE_DARK -> {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                    val uiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                    uiModeManager.setApplicationNightMode(UiModeManager.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
            }
        }
    }
}

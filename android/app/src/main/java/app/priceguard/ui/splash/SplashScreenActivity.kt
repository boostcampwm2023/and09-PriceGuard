package app.priceguard.ui.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import app.priceguard.databinding.ActivitySplashScreenBinding
import app.priceguard.ui.home.HomeActivity
import app.priceguard.ui.intro.IntroActivity
import app.priceguard.ui.util.lifecycle.repeatOnCreated
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val splashViewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppTheme()
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeState()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Use SplashScreen API
            useSplashScreenAPI()
        }
    }

    private fun <T : Activity> launchActivityAndExit(context: Context, clazz: Class<T>) {
        val intent = Intent(context, clazz)
        startActivity(intent)
        finish()
    }

    private fun observeState() {
        repeatOnCreated {
            splashViewModel.event.collect { event ->
                when (event) {
                    SplashScreenViewModel.SplashEvent.OpenHome -> {
                        launchActivityAndExit(
                            this@SplashScreenActivity,
                            HomeActivity::class.java
                        )
                    }

                    SplashScreenViewModel.SplashEvent.OpenIntro -> {
                        launchActivityAndExit(
                            this@SplashScreenActivity,
                            IntroActivity::class.java
                        )
                    }
                }
            }
        }
    }

    private fun useSplashScreenAPI() {
        val onPreDrawListener = ViewTreeObserver.OnPreDrawListener { splashViewModel.isReady.value }
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(onPreDrawListener)
    }

    private fun initAppTheme() {
        val sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val dynamicColorMode = sharedPreferences.getString("DynamicColor", "default")
        val darkMode = sharedPreferences.getString("DarkMode", "system")

        when (dynamicColorMode) {
            "dynamicColor" -> {
                DynamicColors.applyToActivitiesIfAvailable(application)
            }
        }

        when (darkMode) {
            "light" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            "dark" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }
}

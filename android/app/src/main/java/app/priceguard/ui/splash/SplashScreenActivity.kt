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
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.ActivitySplashScreenBinding
import app.priceguard.ui.home.HomeActivity
import app.priceguard.ui.intro.IntroActivity
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenRepository: TokenRepository
    private lateinit var binding: ActivitySplashScreenBinding
    private val splashViewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeState()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            // Use SplashScreen API
            useSplashScreenAPI()
        }
    }

    private fun useSplashScreenAPI() {
        val onPreDrawListener = ViewTreeObserver.OnPreDrawListener { splashViewModel.isReady.value }
        val content: View = findViewById(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(onPreDrawListener)
    }

    private fun observeState() {
        repeatOnStarted {
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

    private fun <T : Activity> launchActivityAndExit(context: Context, clazz: Class<T>) {
        val intent = Intent(context, clazz)
        startActivity(intent)
        finish()
    }
}

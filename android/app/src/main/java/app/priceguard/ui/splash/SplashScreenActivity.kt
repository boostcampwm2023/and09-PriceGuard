package app.priceguard.ui.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.ActivitySplashScreenBinding
import app.priceguard.ui.home.HomeActivity
import app.priceguard.ui.intro.IntroActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenRepository: TokenRepository
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val accessToken = tokenRepository.getAccessToken()
            val refreshToken = tokenRepository.getRefreshToken()

            if (accessToken == null || refreshToken == null) {
                // Intro Activity로 이동
                tokenRepository.clearTokens()
                launchActivityAndExit(this@SplashScreenActivity, IntroActivity::class.java)
                return@launch
            }

            // TODO: Token 갱신하기

            // Main Activity로 이동
            launchActivityAndExit(this@SplashScreenActivity, HomeActivity::class.java)
        }
    }

    private fun <T : Activity> launchActivityAndExit(context: Context, clazz: Class<T>) {
        val intent = Intent(context, clazz)
        startActivity(intent)
        finish()
    }
}

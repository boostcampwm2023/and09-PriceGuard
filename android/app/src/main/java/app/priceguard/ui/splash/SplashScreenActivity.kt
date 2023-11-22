package app.priceguard.ui.splash

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import app.priceguard.R
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.ActivitySplashScreenBinding
import app.priceguard.ui.intro.IntroActivity
import app.priceguard.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenRepository: TokenRepository
    private lateinit var binding: ActivitySplashScreenBinding
    private var isReady = false
    var accessToken: String? = null
    var refreshToken: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)

        lifecycleScope.launch {
            accessToken = tokenRepository.getAccessToken()
            refreshToken = tokenRepository.getRefreshToken()
            isReady = true

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.S) {
                useActivitySplashScreen()
            }
        }

        val content: View = findViewById(android.R.id.content)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            content.viewTreeObserver.addOnPreDrawListener(onPreDrawListener(content))
        }
    }

    private suspend fun useActivitySplashScreen() {
        if (accessToken == null || refreshToken == null) {
            // Intro Activity로 이동
            tokenRepository.clearTokens()
            launchActivityAndExit(this@SplashScreenActivity, IntroActivity::class.java)
            return
        }

        // TODO: Token 갱신하기

        // Main Activity로 이동
        launchActivityAndExit(this@SplashScreenActivity, MainActivity::class.java)
    }

    private fun onPreDrawListener(content: View) =
        object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (!isReady) {
                    return false
                }

                if (accessToken == null || refreshToken == null) {
                    return directToIntro(content)
                }

                return directToMain(content)
            }
        }

    private fun ViewTreeObserver.OnPreDrawListener.directToIntro(content: View): Boolean {
        lifecycleScope.launch {
            tokenRepository.clearTokens()
        }
        launchActivityAndExit(this@SplashScreenActivity, IntroActivity::class.java)
        content.viewTreeObserver.removeOnPreDrawListener(this)
        return true
    }

    private fun ViewTreeObserver.OnPreDrawListener.directToMain(content: View): Boolean {
        launchActivityAndExit(this@SplashScreenActivity, MainActivity::class.java)
        content.viewTreeObserver.removeOnPreDrawListener(this)
        return true
    }

    private fun <T : Activity> launchActivityAndExit(context: Context, clazz: Class<T>) {
        val intent = Intent(context, clazz)
        startActivity(intent)
        finish()
    }
}

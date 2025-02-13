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
import app.priceguard.databinding.ActivitySplashScreenBinding
import app.priceguard.ui.detail.DetailActivity
import app.priceguard.ui.home.HomeActivity
import app.priceguard.ui.intro.IntroActivity
import app.priceguard.ui.util.SystemNavigationColorState
import app.priceguard.ui.util.applySystemNavigationBarColor
import app.priceguard.ui.util.lifecycle.repeatOnCreated
import dagger.hilt.android.AndroidEntryPoint

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val splashViewModel: SplashScreenViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.applySystemNavigationBarColor(SystemNavigationColorState.SURFACE)
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
                        val productShop = intent.getStringExtra("shop")
                        val productCode = intent.getStringExtra("productCode")
                        if (productShop != null && productCode != null) {
                            receivePushAlarm(productShop, productCode)
                        } else {
                            launchActivityAndExit(
                                this@SplashScreenActivity,
                                HomeActivity::class.java
                            )
                        }
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

    private fun receivePushAlarm(productShop: String, productCode: String) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("productShop", productShop)
        intent.putExtra("productCode", productCode)
        intent.putExtra("directed", true)
        startActivity(intent)
    }
}

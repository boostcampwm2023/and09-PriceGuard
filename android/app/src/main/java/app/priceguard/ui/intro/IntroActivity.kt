package app.priceguard.ui.intro

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.databinding.ActivityIntroBinding
import app.priceguard.ui.intro.IntroViewModel.Event
import app.priceguard.ui.login.LoginActivity
import app.priceguard.ui.signup.SignupActivity
import app.priceguard.ui.util.lifecycle.repeatOnStarted

class IntroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityIntroBinding
    private val viewModel: IntroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel

        repeatOnStarted {
            viewModel.eventFlow.collect { event ->
                handleEvent(event)
            }
        }
    }

    private fun handleEvent(event: Event) {
        when (event) {
            is Event.StartLoginActivity -> {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }

            is Event.StartSignupActivity -> {
                val intent = Intent(this, SignupActivity::class.java)
                startActivity(intent)
            }
        }
    }
}

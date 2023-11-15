package app.priceguard.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import app.priceguard.R
import app.priceguard.databinding.ActivityLoginBinding
import app.priceguard.ui.signup.SignupActivity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private var _binding: ActivityLoginBinding? = null
    val binding: ActivityLoginBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.event.collect {
                    when (it) {
                        LoginViewModel.LoginEvent.SignUp -> startActivity(Intent(baseContext, SignupActivity::class.java))
                    }
                }
            }
        }

        _binding = DataBindingUtil.setContentView<ActivityLoginBinding>(this, R.layout.activity_login).apply {
            viewModel = loginViewModel
        }
        setContentView(binding.root)
    }
}

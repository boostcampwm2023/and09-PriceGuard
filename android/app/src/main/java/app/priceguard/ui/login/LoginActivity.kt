package app.priceguard.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.R
import app.priceguard.databinding.ActivityLoginBinding
import app.priceguard.ui.signup.SignupActivity
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        with(binding) {
            viewModel = loginViewModel
        }
        initListener()
        collectEvent()
        setContentView(binding.root)
    }

    private fun initListener() {
        with(binding) {
            btnLoginLogin.setOnClickListener {
                loginViewModel.login()
            }
            btnLoginSignup.setOnClickListener {
                gotoSignUp()
            }
        }
    }

    private fun collectEvent() {
        repeatOnStarted {
            loginViewModel.event.collect {
                when (it) {
                    LoginViewModel.LoginEvent.Invalid -> showDialog(getString(R.string.login_fail), getString(R.string.login_fail_message), getString(R.string.login_fail_accept))
                    LoginViewModel.LoginEvent.LoginFailed -> TODO()
                    LoginViewModel.LoginEvent.LoginSuccess -> TODO()
                }
            }
        }
    }

    private fun showDialog(title: String, message: String, buttonText: String) {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { _, _ -> }.create().show()
    }

    private fun gotoSignUp() {
        startActivity(Intent(this, SignupActivity::class.java))
    }
}

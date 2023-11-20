package app.priceguard.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.R
import app.priceguard.databinding.ActivityLoginBinding
import app.priceguard.ui.login.LoginViewModel.LoginEvent
import app.priceguard.ui.main.MainActivity
import app.priceguard.ui.signup.SignupActivity
import app.priceguard.ui.util.drawable.getCircularProgressIndicatorDrawable
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
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
                (binding.btnLoginLogin as MaterialButton).icon = getCircularProgressIndicatorDrawable(this@LoginActivity)
            }
            btnLoginSignup.setOnClickListener {
                gotoSignUp()
            }
        }
    }

    private fun collectEvent() {
        repeatOnStarted {
            loginViewModel.event.collect { eventType ->
                when (eventType) {
                    LoginEvent.LoginStart -> {
                        (binding.btnLoginLogin as MaterialButton).icon = getCircularProgressIndicatorDrawable(this@LoginActivity)
                    }

                    else -> {
                        (binding.btnLoginLogin as MaterialButton).icon = null
                        setDialogMessageAndShow(eventType)
                    }
                }
            }
        }
    }

    private fun setDialogMessageAndShow(eventType: LoginEvent) {
        when (eventType) {
            LoginEvent.Invalid -> {
                showDialog(
                    getString(R.string.login_invalid),
                    getString(R.string.login_invalid_message)
                )
            }

            is LoginEvent.LoginFailure -> {
                showDialog(getString(R.string.login_fail), getString(R.string.login_fail_message))
            }

            is LoginEvent.LoginSuccess -> {
                val response = eventType.response

                if (response == null) {
                    showDialog(
                        getString(R.string.login_fail),
                        getString(R.string.login_fail_message)
                    )
                } else {
                    loginViewModel.saveTokens(response.accessToken, response.refreshToken)
                }
            }

            is LoginEvent.LoginInfoSaved -> {
                gotoHomeActivity()
            }

            else -> {}
        }
    }

    private fun showDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.confirm)) { _, _ -> }
            .create()
            .show()
    }

    private fun gotoSignUp() {
        startActivity(Intent(this, SignupActivity::class.java))
    }

    private fun gotoHomeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

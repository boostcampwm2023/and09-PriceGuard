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
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
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
                setLoginButtonActive(false, getProgressIndicatorDrawable())
            }
            btnLoginSignup.setOnClickListener {
                gotoSignUp()
            }
        }
    }

    private fun getProgressIndicatorDrawable(): IndeterminateDrawable<CircularProgressIndicatorSpec> {
        val spec = CircularProgressIndicatorSpec(
            this,
            null,
            0,
            R.style.Theme_PriceGuard_CircularProgressIndicator
        )
        return IndeterminateDrawable.createCircularDrawable(this, spec)
    }

    private fun collectEvent() {
        repeatOnStarted {
            loginViewModel.event.collect { eventType ->
                when (eventType) {
                        setLoginButtonActive(false, getProgressIndicatorDrawable())
                    LoginEvent.LoginStart -> {
                    }

                    else -> {
                        setLoginButtonActive(true, null)
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

            is LoginEvent.LoginError -> {
                showDialog(getString(R.string.login_fail), getString(R.string.login_fail_message))
            }

            is LoginEvent.LoginSuccess -> {
                gotoHome()
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

    private fun setLoginButtonActive(
        active: Boolean,
        icon: IndeterminateDrawable<CircularProgressIndicatorSpec>?
    ) {
        (binding.btnLoginLogin as MaterialButton).icon = icon
        binding.btnLoginLogin.isEnabled = active
    }

    private fun gotoSignUp() {
        startActivity(Intent(this, SignupActivity::class.java))
    }

    private fun gotoHome() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

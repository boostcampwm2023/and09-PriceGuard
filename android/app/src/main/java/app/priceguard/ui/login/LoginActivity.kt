package app.priceguard.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.R
import app.priceguard.databinding.ActivityLoginBinding
import app.priceguard.ui.signup.SignupActivity
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable

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
            com.google.android.material.R.style.Widget_Material3_CircularProgressIndicator_ExtraSmall
        )
        return IndeterminateDrawable.createCircularDrawable(this, spec)
    }

    private fun collectEvent() {
        repeatOnStarted {
            loginViewModel.event.collect { eventType ->
                when (eventType) {
                    LoginViewModel.LoginEvent.StartLoading -> setLoginButtonActive(false, getProgressIndicatorDrawable())
                    LoginViewModel.LoginEvent.Invalid -> showDialog(
                        getString(R.string.login_invalid),
                        getString(R.string.login_invalid_message),
                        getString(R.string.login_fail_accept)
                    )

                    LoginViewModel.LoginEvent.LoginFailed -> showDialog(
                        getString(R.string.login_fail),
                        getString(R.string.login_fail_message),
                        getString(R.string.login_fail_accept)
                    )

                    LoginViewModel.LoginEvent.LoginSuccess -> TODO("로그인 성공 시 처리")
                }
            }
        }
    }

    private fun showDialog(title: String, message: String, buttonText: String) {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { _, _ -> setLoginButtonActive(true, null) }
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
}

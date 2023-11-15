package app.priceguard.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import app.priceguard.R
import app.priceguard.databinding.ActivityLoginBinding
import app.priceguard.ui.signup.SignupActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        val loginFailDialog =
            MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_App_MaterialAlertDialog)
                .setTitle("로그인 실패")
                .setMessage("다시 시도해 주세요.")
                .setPositiveButton("확인") { _, _ -> }
                .create()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.event.collect {
                    when (it) {
                        LoginViewModel.LoginEvent.GoToSignUp -> startActivity(
                            Intent(
                                context,
                                SignupActivity::class.java
                            )
                        )

                        LoginViewModel.LoginEvent.LoginFailed -> loginFailDialog.show()
                    }
                }
            }
        }

        binding = ActivityLoginBinding.inflate(layoutInflater).apply {
            viewModel = loginViewModel
            btnLoginLogin.setOnClickListener { loginViewModel.logIn() }
            btnLoginSignup.setOnClickListener { loginViewModel.signUp() }
        }
        setContentView(binding.root)
    }
}

package app.priceguard.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.R
import app.priceguard.databinding.ActivityLoginBinding
import app.priceguard.ui.signup.SignupActivity
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
        setContentView(binding.root)
    }

    private fun initListener() {
        with(binding) {
            btnLoginLogin.setOnClickListener {
                loginViewModel.login {
                    showDialog()
                }
            }
            btnLoginSignup.setOnClickListener {
                gotoSignUp()
            }
        }
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(getString(R.string.login_fail))
            .setMessage(getString(R.string.login_fail_message))
            .setPositiveButton(getString(R.string.login_fail_accept)) { _, _ -> }.create().show()
    }

    private fun gotoSignUp() {
        startActivity(Intent(this, SignupActivity::class.java))
    }
}

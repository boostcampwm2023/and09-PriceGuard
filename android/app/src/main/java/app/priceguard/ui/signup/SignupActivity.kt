package app.priceguard.ui.signup

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import app.priceguard.R
import app.priceguard.databinding.ActivitySignupBinding
import app.priceguard.ui.signup.SignupViewModel.SignupUIState
import kotlinx.coroutines.launch

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.vm = signupViewModel
        binding.lifecycleOwner = this

        setNavigationButton()
        observeState()
    }

    private fun setNavigationButton() {
        binding.mtSignupTopbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                signupViewModel.state.collect { state ->
                    updateNameTextFieldUI(state)
                    updateEmailTextFieldUI(state)
                    updatePasswordTextFieldUI(state)
                    updateRetypePasswordTextFieldUI(state)
                }
            }
        }
    }

    private fun updateNameTextFieldUI(state: SignupUIState) {
        when (state.isNameError) {
            true -> {
                binding.tilSignupName.error = getString(R.string.name_required)
            }

            else -> {
                binding.tilSignupName.error = null
            }
        }
    }

    private fun updateEmailTextFieldUI(state: SignupUIState) {
        when (state.isEmailError) {
            null -> {
                binding.tilSignupEmail.error = null
                binding.tilSignupEmail.helperText = " "
            }

            true -> {
                binding.tilSignupEmail.error = getString(R.string.invalid_email)
            }

            false -> {
                binding.tilSignupEmail.error = null
                binding.tilSignupEmail.helperText = getString(R.string.valid_email)
            }
        }
    }

    private fun updatePasswordTextFieldUI(state: SignupUIState) {
        when (state.isPasswordError) {
            null -> {
                binding.tilSignupPassword.error = null
                binding.tilSignupPassword.helperText = " "
            }

            true -> {
                binding.tilSignupPassword.error = getString(R.string.invalid_password)
            }

            false -> {
                binding.tilSignupPassword.error = null
                binding.tilSignupPassword.helperText = getString(R.string.valid_password)
            }
        }
    }

    private fun updateRetypePasswordTextFieldUI(state: SignupUIState) {
        when (state.isRetypePasswordError) {
            null -> {
                binding.tilSignupRetypePassword.error = null
                binding.tilSignupRetypePassword.helperText = " "
            }

            true -> {
                binding.tilSignupRetypePassword.error = getString(R.string.password_mismatch)
            }

            false -> {
                binding.tilSignupRetypePassword.error = null
                binding.tilSignupRetypePassword.helperText = getString(R.string.password_match)
            }
        }
    }
}

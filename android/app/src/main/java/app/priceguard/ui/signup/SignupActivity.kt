package app.priceguard.ui.signup

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import app.priceguard.R
import app.priceguard.data.dto.SignupState
import app.priceguard.databinding.ActivitySignupBinding
import app.priceguard.ui.home.HomeActivity
import app.priceguard.ui.signup.SignupViewModel.SignupEvent
import app.priceguard.ui.signup.SignupViewModel.SignupUIState
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.ui.drawable.getCircularProgressIndicatorDrawable
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.vm = signupViewModel
        binding.lifecycleOwner = this
        setNavigationButton()
        disableAppBarScroll()
        observeState()
    }

    private fun disableAppBarScroll() {
        val clLayoutParams = binding.ablSignupTopbar.layoutParams as CoordinatorLayout.LayoutParams
        val scrollView: NestedScrollView = binding.nsvSignupContent
        val viewTreeObserver = scrollView.viewTreeObserver
        val disabledAblBehavior = getAblBehavior(false)
        val enabledAblBehavior = getAblBehavior(true)

        viewTreeObserver.addOnGlobalLayoutListener {
            if (scrollView.measuredHeight - scrollView.getChildAt(0).height >= 0) {
                clLayoutParams.behavior = disabledAblBehavior
            } else {
                clLayoutParams.behavior = enabledAblBehavior
            }
        }
    }

    private fun getAblBehavior(canDrag: Boolean): AppBarLayout.Behavior {
        val ablBehavior = AppBarLayout.Behavior()
        ablBehavior.setDragCallback(object : DragCallback() {
            override fun canDrag(appBarLayout: AppBarLayout): Boolean {
                return canDrag
            }
        })
        return ablBehavior
    }

    private fun handleSignupEvent(event: SignupEvent) {
        val circularProgressIndicator = getCircularProgressIndicatorDrawable(this)

        when (event) {
            is SignupEvent.SignupStart -> {
                (binding.btnSignupSignup as MaterialButton).icon = circularProgressIndicator
            }

            is SignupEvent.SignupSuccess -> {
                (binding.btnSignupSignup as MaterialButton).icon = null
            }

            is SignupEvent.SignupFailure -> {
                (binding.btnSignupSignup as MaterialButton).icon = null
                when (event.errorState) {
                    SignupState.INVALID_PARAMETER -> {
                        showDialog(getString(R.string.error), getString(R.string.invalid_parameter))
                    }

                    SignupState.DUPLICATE_EMAIL -> {
                        showDialog(getString(R.string.error), getString(R.string.duplicate_email))
                    }

                    SignupState.UNDEFINED_ERROR -> {
                        showDialog(getString(R.string.error), getString(R.string.undefined_error))
                    }

                    else -> {}
                }
            }

            SignupEvent.SignupInfoSaved -> {
                gotoHomeActivity()
            }
        }
    }

    private fun gotoHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setNavigationButton() {
        binding.mtSignupTopbar.setNavigationOnClickListener {
            finish()
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

    private fun observeState() {
        repeatOnStarted {
            signupViewModel.state.collect { state ->
                updateNameTextFieldUI(state)
                updateEmailTextFieldUI(state)
                updatePasswordTextFieldUI(state)
                updateRetypePasswordTextFieldUI(state)
            }
        }

        repeatOnStarted {
            signupViewModel.eventFlow.collect { event ->
                handleSignupEvent(event)
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

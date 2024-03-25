package app.priceguard.ui.signup

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import app.priceguard.R
import app.priceguard.databinding.ActivitySignupBinding
import app.priceguard.ui.home.HomeActivity
import app.priceguard.ui.login.findpassword.EmailVerificationViewModel
import app.priceguard.ui.signup.SignupViewModel.SignupEvent
import app.priceguard.ui.util.SystemNavigationColorState
import app.priceguard.ui.util.applySystemNavigationBarColor
import app.priceguard.ui.util.drawable.getCircularProgressIndicatorDrawable
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.showConfirmDialog
import app.priceguard.ui.util.showDialogWithAction
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.Behavior.DragCallback
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signupViewModel: SignupViewModel by viewModels()
    private val emailVerificationViewModel: EmailVerificationViewModel by viewModels()
    private lateinit var circularProgressIndicator: IndeterminateDrawable<CircularProgressIndicatorSpec>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.applySystemNavigationBarColor(SystemNavigationColorState.SURFACE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_signup)
        binding.vm = signupViewModel
        binding.verificationViewModel = emailVerificationViewModel
        binding.lifecycleOwner = this
        circularProgressIndicator = getCircularProgressIndicatorDrawable(this@SignupActivity)
        setNavigationButton()
        disableAppBarScroll()
        observeState()
    }

    override fun onDestroy() {
        super.onDestroy()
        circularProgressIndicator.stop()
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
        when (event) {
            is SignupEvent.SignupStart -> {
                (binding.btnSignupSignup as MaterialButton).icon = circularProgressIndicator
            }

            else -> {
                (binding.btnSignupSignup as MaterialButton).icon = null
                when (event) {
                    SignupEvent.SignupInfoSaved -> {
                        gotoHomeActivity()
                    }

                    SignupEvent.DuplicatedEmail -> {
                        showConfirmDialog(
                            getString(R.string.error),
                            getString(R.string.duplicate_email)
                        )
                    }

                    SignupEvent.InvalidRequest -> {
                        showConfirmDialog(
                            getString(R.string.error),
                            getString(R.string.invalid_parameter)
                        )
                    }

                    SignupEvent.UndefinedError -> {
                        showConfirmDialog(
                            getString(R.string.error),
                            getString(R.string.undefined_error)
                        )
                    }

                    SignupEvent.TokenUpdateError, SignupEvent.FirebaseError -> {
                        Toast.makeText(
                            this@SignupActivity,
                            getString(R.string.push_notification_not_working),
                            Toast.LENGTH_LONG
                        ).show()
                    }

                    else -> {}
                }
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

    private fun observeState() {
        repeatOnStarted {
            signupViewModel.eventFlow.collect { event ->
                handleSignupEvent(event)
            }
        }
        repeatOnStarted {
            emailVerificationViewModel.event.collect { event ->
                when (event) {
                    EmailVerificationViewModel.EmailVerificationEvent.SuccessRequestVerificationCode -> {
                        Toast.makeText(
                            this@SignupActivity,
                            getString(R.string.sent_verification_code),
                            Toast.LENGTH_LONG
                        ).show()
                        startTimer(180)
                    }

                    EmailVerificationViewModel.EmailVerificationEvent.DuplicatedEmail -> {
                        showDialogWithAction(
                            getString(R.string.error_email),
                            getString(R.string.duplicate_email)
                        )
                    }

                    EmailVerificationViewModel.EmailVerificationEvent.WrongVerificationCode -> {
                        showDialogWithAction(
                            getString(R.string.error_verify_email),
                            getString(R.string.match_error_verification_code)
                        )
                    }

                    EmailVerificationViewModel.EmailVerificationEvent.OverVerificationLimit -> {
                        showDialogWithAction(
                            getString(R.string.error_request_verification_code),
                            getString(R.string.request_verification_code_limit_max_5)
                        )
                    }

                    EmailVerificationViewModel.EmailVerificationEvent.ExpireToken -> {
                        showDialogWithAction(
                            getString(R.string.error_verify_email),
                            getString(R.string.expire_verification_code)
                        )
                    }

                    else -> {
                        showDialogWithAction(
                            getString(R.string.error),
                            getString(R.string.undefined_error)
                        )
                    }
                }
            }
        }
    }

    private fun startTimer(totalTimeInSeconds: Int) {
        val countDownTimer = object : CountDownTimer((totalTimeInSeconds * 1000).toLong(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                val minutes = TimeUnit.SECONDS.toMinutes(timeLeft)
                val seconds = timeLeft - TimeUnit.MINUTES.toSeconds(minutes)

                emailVerificationViewModel.updateTimer(
                    getString(
                        R.string.finish_send_verification_code,
                        String.format("%02d:%02d", minutes, seconds)
                    )
                )
            }

            override fun onFinish() {
                emailVerificationViewModel.updateTimer(getString(R.string.expired))
                emailVerificationViewModel.updateRetryVerificationCodeEnabled(true)
            }
        }
        countDownTimer.start()
    }
}

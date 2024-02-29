package app.priceguard.ui.login.findpassword

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.databinding.FragmentEmailVerificatioinBinding
import app.priceguard.ui.login.findpassword.EmailVerificationViewModel.EmailVerificationEvent
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.safeNavigate
import app.priceguard.ui.util.showDialogWithAction
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class EmailVerificationFragment : Fragment() {

    private var _binding: FragmentEmailVerificatioinBinding? = null
    private val binding get() = _binding!!

    private val emailVerificationViewModel: EmailVerificationViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerificatioinBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = emailVerificationViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initCollector()
    }

    private fun initView() {
        binding.btnEmailVerificationBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initCollector() {
        viewLifecycleOwner.repeatOnStarted {
            emailVerificationViewModel.event.collect { event ->
                when (event) {
                    EmailVerificationEvent.SuccessRequestVerificationCode -> {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.sended_verification_code),
                            Toast.LENGTH_LONG
                        ).show()
                        startTimer(180)
                    }

                    EmailVerificationEvent.SuccessVerify -> {
                        goToResetPassword()
                    }

                    EmailVerificationEvent.NotFoundEmail -> {
                        showDialogWithAction(
                            getString(R.string.error_request_verification_code),
                            getString(R.string.invalid_email)
                        )
                    }

                    EmailVerificationEvent.WrongVerificationCode -> {
                        showDialogWithAction(
                            getString(R.string.error_verify_email),
                            getString(R.string.match_error_verification_code)
                        )
                    }

                    EmailVerificationEvent.OverVerificationLimit -> {
                        showDialogWithAction(
                            getString(R.string.error_request_verification_code),
                            getString(R.string.request_verification_code_limit_max_5)
                        )
                    }

                    EmailVerificationEvent.UndefinedError -> {
                        showDialogWithAction(
                            getString(R.string.error),
                            getString(R.string.undefined_error)
                        )
                    }

                    EmailVerificationEvent.ExpireToken -> {
                        showDialogWithAction(
                            getString(R.string.error_verify_email),
                            getString(R.string.expire_verification_code)
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

                emailVerificationViewModel.updateTimer(String.format("%02d:%02d", minutes, seconds))
            }

            override fun onFinish() {
                emailVerificationViewModel.updateTimer("0")
            }
        }
        countDownTimer.start()
    }

    private fun goToResetPassword() {
        val action =
            EmailVerificationFragmentDirections.actionEmailVerificationFragmentToResetPasswordFragment(
                verifyToken = emailVerificationViewModel.state.value.verifyToken
            )
        findNavController().safeNavigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

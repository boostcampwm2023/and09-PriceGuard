package app.priceguard.ui.login.findpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.databinding.FragmentResetPasswordBinding
import app.priceguard.ui.data.DialogConfirmAction
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.showDialogWithAction
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!

    private val resetPasswordViewModel: ResetPasswordViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(layoutInflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = resetPasswordViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().navigateUp()
        }
        initView()
        initCollector()
        setVerifyToken()
    }

    private fun initView() {
        binding.btnResetPasswordBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun initCollector() {
        viewLifecycleOwner.repeatOnStarted {
            resetPasswordViewModel.event.collect { event ->
                when (event) {
                    ResetPasswordViewModel.ResetPasswordEvent.SuccessResetPassword -> {
                        showDialogWithAction(
                            getString(R.string.finish_find_password),
                            getString(R.string.success_reset_password_retry_login),
                            DialogConfirmAction.FINISH
                        )
                    }

                    ResetPasswordViewModel.ResetPasswordEvent.ErrorVerifyToken -> {
                        showDialogWithAction(
                            getString(R.string.error),
                            getString(R.string.invalid_access),
                            DialogConfirmAction.FINISH
                        )
                    }

                    ResetPasswordViewModel.ResetPasswordEvent.UndefinedError -> {
                        showDialogWithAction(
                            getString(R.string.error),
                            getString(R.string.undefined_error)
                        )
                    }
                }
            }
        }
    }

    private fun setVerifyToken() {
        val arguments = requireArguments()
        val verifyToken = arguments.getString("verifyToken")?: ""
        resetPasswordViewModel.updateVerifyToken(verifyToken)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package app.priceguard.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.priceguard.databinding.FragmentEmailVerificatioinBinding
import app.priceguard.ui.util.safeNavigate

class EmailVerificationFragment : Fragment() {

    private var _binding: FragmentEmailVerificatioinBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEmailVerificatioinBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEmailVerificationBack.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnEmailVerificationNext.setOnClickListener {
            // TODO: 요청 보내기
            goToResetPassword()
        }
    }

    private fun goToResetPassword() {
        val action =
            EmailVerificationFragmentDirections.actionEmailVerificationFragmentToResetPasswordFragment(
                passwordToken = ""
            )
        findNavController().safeNavigate(action)
    }
}

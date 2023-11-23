package app.priceguard.ui.additem.link

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.databinding.FragmentRegisterItemLinkBinding
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class RegisterItemLinkFragment : Fragment() {

    private var _binding: FragmentRegisterItemLinkBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterItemLinkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterItemLinkBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        setCollector()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setCollector() {
        repeatOnStarted {
            viewModel.state.collect { state ->
                if (state.product != null) {
                    // TODO: 링크 정규식 검사에 맞지 않을 시 발생하는 ui 업데이트 로직
                }
            }
        }

        repeatOnStarted {
            viewModel.event.collect { event ->
                when (event) {
                    is RegisterItemLinkViewModel.RegisterLinkEvent.SuccessVerification -> {
                        val action =
                            RegisterItemLinkFragmentDirections.actionRegisterItemLinkFragmentToConfirmItemLinkFragment(
                                Json.encodeToString(event.product)
                            )
                        findNavController().safeNavigate(action)
                    }

                    is RegisterItemLinkViewModel.RegisterLinkEvent.FailureVerification -> {
                        Toast.makeText(
                            requireActivity(),
                            getString(R.string.not_link),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

    private fun updateLinkFieldUI() {
    }

    private fun updateNextBtnUI() {
    }

    private fun update() {
    }
}

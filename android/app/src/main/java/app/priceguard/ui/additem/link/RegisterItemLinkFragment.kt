package app.priceguard.ui.additem.link

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        initCollector()
        initEvent()
    }

    private fun initCollector() {
        repeatOnStarted {
            viewModel.state.collect { state ->
                if (state.isLinkError) {
                    updateLinkError(getString(R.string.not_link))
                } else {
                    updateLinkError("", false)
                }
            }
        }
    }

    private fun initEvent() {
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
                        updateLinkError(getString(R.string.not_product_link))
                    }
                }
            }
        }
    }

    private fun updateLinkError(message: String, isEnabled: Boolean = true) {
        binding.viewEditTextBackground.background = if (isEnabled) {
            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner_error)
        } else {
            ContextCompat.getDrawable(requireActivity(), R.drawable.bg_round_corner)
        }
        binding.tvRegisterItemError.text = message
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
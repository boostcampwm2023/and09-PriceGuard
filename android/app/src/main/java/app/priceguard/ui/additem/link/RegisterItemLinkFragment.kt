package app.priceguard.ui.additem.link

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.data.dto.ProductErrorState
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.FragmentRegisterItemLinkBinding
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.ui.showPermissionDeniedDialog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class RegisterItemLinkFragment : Fragment() {

    @Inject
    lateinit var tokenRepository: TokenRepository

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
                    updateLinkError("")
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
                        when (event.errorType) {
                            ProductErrorState.PERMISSION_DENIED -> {
                                requireActivity().showPermissionDeniedDialog(tokenRepository)
                            }

                            ProductErrorState.INVALID_REQUEST -> {
                                updateLinkError(getString(R.string.not_product_link))
                            }

                            else -> {
                                updateLinkError(getString(R.string.undefined_error))
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateLinkError(message: String) {
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

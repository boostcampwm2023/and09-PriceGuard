package app.priceguard.ui.additem.link

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.databinding.FragmentRegisterItemLinkBinding
import app.priceguard.ui.home.HomeActivity
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.showDialogWithLogout
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterItemLinkFragment : Fragment() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    private var _binding: FragmentRegisterItemLinkBinding? = null
    private val binding get() = _binding!!
    private val registerItemLinkViewModel: RegisterItemLinkViewModel by viewModels()

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
        binding.viewModel = registerItemLinkViewModel

        setBackPressedCallback()
        setLinkText()

        initCollector()
        initEvent()

        binding.tvRegisterItemHelper.setOnClickListener {
            val intent = Intent(requireActivity(), LinkHelperWebViewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setLinkText() {
        arguments?.getString("link")?.let { linkText ->
            binding.etRegisterItemLink.setText(linkText)
            registerItemLinkViewModel.updateLink(linkText)
        }
    }

    private fun setBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            goToHomeActivity()
        }
    }

    private fun goToHomeActivity() {
        val activityIntent = requireActivity().intent
        if (activityIntent?.action == Intent.ACTION_SEND) {
            val intent = Intent(requireActivity(), HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        requireActivity().finish()
    }

    private fun initCollector() {
        repeatOnStarted {
            registerItemLinkViewModel.state.collect { state ->
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
            registerItemLinkViewModel.event.collect { event ->
                when (event) {
                    is RegisterItemLinkViewModel.RegisterLinkEvent.SuccessVerification -> {
                        val action =
                            RegisterItemLinkFragmentDirections.actionRegisterItemLinkFragmentToConfirmItemLinkFragment(
                                event.product.productCode,
                                event.product.productPrice,
                                event.product.shop,
                                event.product.productName,
                                event.product.imageUrl
                            )
                        findNavController().safeNavigate(action)
                    }

                    is RegisterItemLinkViewModel.RegisterLinkEvent.FailureVerification -> {
                        when (event.errorType) {
                            ProductErrorState.PERMISSION_DENIED -> {
                                showDialogWithLogout()
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

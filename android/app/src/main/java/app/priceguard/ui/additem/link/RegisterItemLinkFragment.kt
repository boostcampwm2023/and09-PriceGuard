package app.priceguard.ui.additem.link

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.databinding.FragmentRegisterItemLinkBinding
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import dagger.hilt.android.AndroidEntryPoint

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
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                }
            }
        }

        repeatOnStarted {
            viewModel.event.collect { event ->
                when (event) {
                    is RegisterItemLinkViewModel.RegisterLinkEvent.SuccessVerification -> {
                        val action =
                            RegisterItemLinkFragmentDirections.actionRegisterItemLinkFragmentToConfirmItemLinkFragment(
                                event.product
                            )
                        findNavController().navigate(action)
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

    private fun updateLinkFieldUI() {
    }

    private fun updateNextBtnUI() {
    }

    private fun update() {
    }
}

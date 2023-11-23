package app.priceguard.ui.main.additem.link

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.data.dto.ProductDTO
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
        initListener()
        setCollector()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListener() {
        binding.btnRegisterItemNext.setOnClickListener {
            val action =
                RegisterItemLinkFragmentDirections.actionRegisterItemLinkFragmentToConfirmItemLinkFragment(
                    ProductDTO("name", "123", 12345, "shop", "image")
                )
            findNavController().navigate(action)
            repeatOnStarted {
                viewModel.verifyLink()
            }
        }
    }

    private fun setCollector() {
        repeatOnStarted {
            viewModel.state.collect { state ->
                if (state.product != null) {
                    val action =
                        RegisterItemLinkFragmentDirections.actionRegisterItemLinkFragmentToConfirmItemLinkFragment(
                            state.product
                        )
                    findNavController().navigate(action)
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

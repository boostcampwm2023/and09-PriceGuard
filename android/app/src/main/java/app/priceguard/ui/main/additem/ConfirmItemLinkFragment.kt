package app.priceguard.ui.main.additem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.databinding.FragmentConfirmItemLinkBinding

class ConfirmItemLinkFragment : Fragment() {

    private var _binding: FragmentConfirmItemLinkBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ConfirmItemLinkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmItemLinkBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initListener()

        (requireActivity() as AddItemActivity).binding.ctlAddItemTopbar.title = getString(R.string.confirm_product_title)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentConfirmItemLinkBinding.initListener() {
        btnConfirmItemNext.setOnClickListener {
            findNavController().navigate(R.id.action_confirmItemLinkFragment_to_setTargetPriceFragment)
        }
        btnConfirmItemBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}

package app.priceguard.ui.additem.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.databinding.FragmentConfirmItemLinkBinding

class ConfirmItemLinkFragment : Fragment() {

    private var _binding: FragmentConfirmItemLinkBinding? = null
    private val binding get() = _binding!!
    private val confirmItemLinkViewModel: ConfirmItemLinkViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmItemLinkBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = confirmItemLinkViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.initListener()
        initView()
    }

    private fun initView() {
        val arguments = requireArguments()
        confirmItemLinkViewModel.setUIState(
            price = arguments.getInt("productPrice"),
            brand = arguments.getString("shop") ?: return,
            name = arguments.getString("productName") ?: return,
            imageUrl = arguments.getString("imageUrl") ?: return
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentConfirmItemLinkBinding.initListener() {
        val arguments = requireArguments()

        btnConfirmItemNext.setOnClickListener {
            val action =
                ConfirmItemLinkFragmentDirections.actionConfirmItemLinkFragmentToSetTargetPriceFragment(
                    arguments.getString("productCode") ?: "",
                    arguments.getString("productName") ?: "",
                    arguments.getInt("productPrice"),
                    true,
                    (arguments.getInt("productPrice") * 0.8).toInt()
                )
            findNavController().navigate(action)
        }
        btnConfirmItemBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

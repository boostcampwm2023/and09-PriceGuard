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
import com.google.android.material.appbar.MaterialToolbar

class ConfirmItemLinkFragment : Fragment() {

    private var _binding: FragmentConfirmItemLinkBinding? = null
    private val binding get() = _binding!!

//    private val navController = findNavController()
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

        requireActivity().findViewById<MaterialToolbar>(R.id.mt_add_item_topbar).setTitle(R.string.confirm)

        binding.btnConfirmItemNext.setOnClickListener {
            findNavController().navigate(R.id.action_confirmItemLinkFragment_to_setTargetPriceFragment)
        }
        binding.btnConfirmItemBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

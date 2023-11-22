package app.priceguard.ui.main.additem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.databinding.FragmentRegisterItemLinkBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterItemLinkFragment : Fragment() {

    private var _binding: FragmentRegisterItemLinkBinding? = null
    private val binding get() = _binding!!

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
        binding.initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentRegisterItemLinkBinding.initListener() {
        btnRegisterItemNext.setOnClickListener {
            findNavController().navigate(R.id.action_registerItemLinkFragment_to_confirmItemLinkFragment)
        }
    }
}

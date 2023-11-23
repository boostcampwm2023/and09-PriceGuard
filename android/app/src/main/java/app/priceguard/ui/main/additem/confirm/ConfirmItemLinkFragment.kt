package app.priceguard.ui.main.additem.confirm

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.data.dto.ProductDTO
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

        val productInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getSerializable("product", ProductDTO::class.java)
        } else {
            requireArguments().getSerializable("product") as ProductDTO
        }

        if (productInfo != null) {
            viewModel.setProductInfo(productInfo)
        } else {
            // TODO: 상품 정보 로드 에러
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentConfirmItemLinkBinding.initListener() {
        btnConfirmItemNext.setOnClickListener {
            val price = 0
            val name = "qeqwe"
            val action =
                ConfirmItemLinkFragmentDirections.actionConfirmItemLinkFragmentToSetTargetPriceFragment(
                    name,
                    price
                )
            findNavController().navigate(action)
        }
        btnConfirmItemBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}

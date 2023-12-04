package app.priceguard.ui.additem.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.databinding.FragmentConfirmItemLinkBinding
import java.text.NumberFormat

class ConfirmItemLinkFragment : Fragment() {

    private var _binding: FragmentConfirmItemLinkBinding? = null
    private val binding get() = _binding!!

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

        binding.initListener()
        binding.initView()
    }

    private fun FragmentConfirmItemLinkBinding.initView() {
        val arguments = requireArguments()
        val productPrice = arguments.getInt("productPrice")
        val shop = arguments.getString("shop") ?: return
        val productName = arguments.getString("productName") ?: return
        val imageUrlString = arguments.getString("imageUrl") ?: return

        tvConfirmItemPrice.text =
            String.format(
                resources.getString(R.string.won),
                NumberFormat.getNumberInstance().format(productPrice)
            )
        tvConfirmItemBrand.text = shop
        tvConfirmItemItemTitle.text = productName
        imageUrl = imageUrlString
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
                    true
                )
            findNavController().navigate(action)
        }
        btnConfirmItemBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

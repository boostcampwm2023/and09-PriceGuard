package app.priceguard.ui.additem.confirm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.data.dto.ProductVerifyDTO
import app.priceguard.databinding.FragmentConfirmItemLinkBinding
import java.text.NumberFormat
import kotlinx.serialization.json.Json

class ConfirmItemLinkFragment : Fragment() {

    private var _binding: FragmentConfirmItemLinkBinding? = null
    private val binding get() = _binding!!

    private lateinit var productInfo: ProductVerifyDTO

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
        val productJson = requireArguments().getString("product") ?: return
        productInfo = Json.decodeFromString(productJson)

        tvConfirmItemPrice.text =
            String.format(
                resources.getString(R.string.won),
                NumberFormat.getNumberInstance().format(productInfo.productPrice)
            )
        tvConfirmItemBrand.text = productInfo.shop
        tvConfirmItemItemTitle.text = productInfo.productName
        imageUrl = productInfo.imageUrl
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentConfirmItemLinkBinding.initListener() {
        btnConfirmItemNext.setOnClickListener {
            val action =
                ConfirmItemLinkFragmentDirections.actionConfirmItemLinkFragmentToSetTargetPriceFragment(
                    productInfo.productCode ?: "",
                    productInfo.productName ?: "",
                    productInfo.productPrice ?: 0,
                    true
                )
            findNavController().navigate(action)
        }
        btnConfirmItemBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

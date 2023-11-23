package app.priceguard.ui.main.additem.setprice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.databinding.FragmentSetTargetPriceBinding
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import kotlinx.coroutines.flow.collect

class SetTargetPriceFragment : Fragment() {

    private var _binding: FragmentSetTargetPriceBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SetTargetPriceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetTargetPriceBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = requireArguments().getString("productTitle")
        val price = requireArguments().getInt("productPrice")
        binding.title = title
        binding.price = price

        binding.slTargetPrice.addOnChangeListener { slider, value, fromUser ->
            if (!binding.etTargetPrice.isFocused) {
                binding.tvTargetPricePercent.text =
                    String.format(getString(R.string.current_price_percent), value.toInt())
                binding.etTargetPrice.setText((price * value.toInt() / 100).toString())
            }
        }
        binding.etTargetPrice.addTextChangedListener {
            if (binding.etTargetPrice.isFocused) {
                if (it.toString().matches("^\\d+\$".toRegex())) {
                    val targetPrice = it.toString().toFloat()
                    var percent = ((targetPrice / price) * 100).toInt()
                    binding.tvTargetPricePercent.text =
                        String.format(getString(R.string.current_price_percent), percent)

                    if (targetPrice > price) {
                        binding.tvTargetPricePercent.text = getString(R.string.over_current_price)
                        percent = 100
                    } else if (percent < 1) {
                        percent = 1
                    }
                    binding.slTargetPrice.value = percent.toFloat()
                }
            }
        }

        binding.initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun FragmentSetTargetPriceBinding.initListener() {
        btnConfirmItemBack.setOnClickListener {
            findNavController().navigateUp()
        }
        btnConfirmItemNext.setOnClickListener {
            viewModel.addProduct()
        }
    }

    private fun handleEvent() {
        repeatOnStarted {
            viewModel.event.collect { event ->
                when (event) {
                    SetTargetPriceViewModel.SetTargetPriceEvent.FailureProductAdd -> {
                    }
                    SetTargetPriceViewModel.SetTargetPriceEvent.SuccessProductAdd -> {
                        // TODO: AddProductItem 액티비티 종료 후 상품 세부 정보 화면으로 이동하기
                    }
                }
            }
        }
    }
}

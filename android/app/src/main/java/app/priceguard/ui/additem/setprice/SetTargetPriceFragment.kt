package app.priceguard.ui.additem.setprice

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
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat

@AndroidEntryPoint
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

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val productCode = requireArguments().getString("productCode") ?: ""
        val title = requireArguments().getString("productTitle") ?: ""
        val price = requireArguments().getInt("productPrice")

        binding.tvSetPriceCurrentPrice.text =
            String.format(
                resources.getString(R.string.won),
                NumberFormat.getNumberInstance().format(price)
            )

        viewModel.setProductInfo(productCode, title, price)
        binding.etTargetPrice.setText((price * 0.8).toInt().toString())

        binding.initListener()
        handleEvent()
    }

    private fun FragmentSetTargetPriceBinding.initListener() {
        btnConfirmItemBack.setOnClickListener {
            findNavController().navigateUp()
        }
        slTargetPrice.addOnChangeListener { slider, value, fromUser ->
            if (!etTargetPrice.isFocused) {
                tvTargetPricePercent.text =
                    String.format(getString(R.string.current_price_percent), value.toInt())
                etTargetPrice.setText(
                    ((viewModel?.state?.value?.productPrice ?: 0) * value.toInt() / 100).toString()
                )
            }
        }
        slTargetPrice.addOnSliderTouchListener(object : OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                binding.etTargetPrice.clearFocus()
            }

            override fun onStopTrackingTouch(slider: Slider) {
            }
        })
        etTargetPrice.addTextChangedListener {
            if (etTargetPrice.isFocused) {
                if (it.toString().matches("^\\d+\$".toRegex())) {
                    val targetPrice = it.toString().toFloat()
                    var percent =
                        ((targetPrice / (viewModel?.state?.value?.productPrice ?: 0)) * 100).toInt()
                    tvTargetPricePercent.text =
                        String.format(getString(R.string.current_price_percent), percent)

                    percent = 10 * ((percent + 5) / 10)
                    if (targetPrice > (viewModel?.state?.value?.productPrice ?: 0)) {
                        tvTargetPricePercent.text = getString(R.string.over_current_price)
                        percent = 100
                    } else if (percent < 1) {
                        percent = 0
                    }
                    slTargetPrice.value = percent.toFloat()
                }
            }
        }
    }

    private fun handleEvent() {
        repeatOnStarted {
            viewModel.event.collect { event ->
                when (event) {
                    SetTargetPriceViewModel.SetTargetPriceEvent.FailureProductAdd -> {
                    }

                    SetTargetPriceViewModel.SetTargetPriceEvent.SuccessProductAdd -> {
                        activity?.finish()
                        // TODO: AddProductItem 액티비티 종료 후 상품 세부 정보 화면으로 이동하기
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

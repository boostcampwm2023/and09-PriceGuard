package app.priceguard.ui.additem.setprice

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.databinding.FragmentSetTargetPriceBinding
import app.priceguard.ui.additem.setprice.SetTargetPriceViewModel.SetTargetPriceEvent
import app.priceguard.ui.data.DialogConfirmAction
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.showDialogWithAction
import app.priceguard.ui.util.showDialogWithLogout
import com.google.android.material.slider.Slider
import com.google.android.material.slider.Slider.OnSliderTouchListener
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import javax.inject.Inject

@AndroidEntryPoint
class SetTargetPriceFragment : Fragment() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    private var _binding: FragmentSetTargetPriceBinding? = null
    private val binding get() = _binding!!
    private val setTargetPriceViewModel: SetTargetPriceViewModel by viewModels()

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

        binding.viewModel = setTargetPriceViewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setBackPressedCallback()
        binding.initView()
        binding.initListener()
        handleEvent()
    }

    private fun setBackPressedCallback() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            if (requireActivity().intent.hasExtra("isAdding")) {
                requireActivity().finish()
            } else {
                findNavController().navigateUp()
            }
        }
    }

    private fun FragmentSetTargetPriceBinding.initView() {
        val arguments = requireArguments()

        val productShop = arguments.getString("productShop") ?: ""
        val productCode = arguments.getString("productCode") ?: ""
        val title = arguments.getString("productTitle") ?: ""
        val price = arguments.getInt("productPrice")
        var targetPrice = arguments.getInt("productTargetPrice")

        setTargetPriceViewModel.updateTargetPrice(targetPrice)

        tvSetPriceCurrentPrice.text =
            String.format(
                resources.getString(R.string.won),
                NumberFormat.getNumberInstance().format(price)
            )
        tvSetPriceCurrentPrice.contentDescription =
            getString(R.string.current_price_info, tvSetPriceCurrentPrice.text)

        setTargetPriceViewModel.setProductInfo(productShop, productCode, title, price)
        etTargetPrice.setText(targetPrice.toString())

        updateSlideValueWithPrice(targetPrice.toFloat())
    }

    private fun FragmentSetTargetPriceBinding.initListener() {
        btnConfirmItemBack.setOnClickListener {
            if (requireActivity().intent.hasExtra("isAdding")) {
                requireActivity().finish()
            } else {
                findNavController().navigateUp()
            }
        }
        btnConfirmItemNext.setOnClickListener {
            val isAdding = requireArguments().getBoolean("isAdding")
            if (isAdding) setTargetPriceViewModel.addProduct() else setTargetPriceViewModel.patchProduct()
        }
        slTargetPrice.addOnChangeListener { _, value, _ ->
            if (!etTargetPrice.isFocused) {
                setTargetPriceAndPercent(value)
            }
        }
        slTargetPrice.addOnSliderTouchListener(object : OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {
                etTargetPrice.clearFocus()
                setTargetPriceAndPercent(slider.value)
            }

            override fun onStopTrackingTouch(slider: Slider) {
            }
        })
        etTargetPrice.addTextChangedListener {
            updateTargetPriceUI(it)
        }
    }

    private fun updateTargetPriceUI(it: Editable?) {
        if (binding.etTargetPrice.isFocused) {
            val targetPrice = if (it.toString().matches("^\\d{1,9}$".toRegex())) {
                it.toString().toInt()
            } else if (it.toString().isEmpty()) {
                binding.etTargetPrice.setText(getString(R.string.min_price))
                0
            } else {
                binding.etTargetPrice.setText(getString(R.string.max_price))
                999999999
            }

            setTargetPriceViewModel.updateTargetPrice(targetPrice)
            binding.updateSlideValueWithPrice(targetPrice.toFloat())
        }
    }

    private fun Int.roundAtFirstDigit(): Int {
        return ((this + 5) / 10) * 10
    }

    private fun FragmentSetTargetPriceBinding.setTargetPriceAndPercent(value: Float) {
        val targetPrice = ((setTargetPriceViewModel.state.value.productPrice) * value.toInt() / 100)
        tvTargetPricePercent.text =
            String.format(getString(R.string.current_price_percent), value.toInt())
        etTargetPrice.setText(
            targetPrice.toString()
        )
        setTargetPriceViewModel.updateTargetPrice(targetPrice)
    }

    private fun handleEvent() {
        repeatOnStarted {
            setTargetPriceViewModel.event.collect { event ->
                when (event) {
                    is SetTargetPriceEvent.SuccessProductAdd -> {
                        showDialogWithAction(
                            getString(R.string.success_add),
                            getString(R.string.success_add_message),
                            DialogConfirmAction.HOME
                        )
                    }

                    is SetTargetPriceEvent.SuccessPriceUpdate -> {
                        showDialogWithAction(
                            getString(R.string.success_update),
                            getString(R.string.success_update_message),
                            DialogConfirmAction.FINISH
                        )
                    }

                    is SetTargetPriceEvent.FailurePriceAdd -> {
                        when (event.errorType) {
                            ProductErrorState.EXIST -> {
                                showDialogWithAction(
                                    getString(R.string.error_add_product),
                                    getString(R.string.exist_product),
                                    DialogConfirmAction.HOME
                                )
                            }

                            ProductErrorState.PERMISSION_DENIED -> {
                                showDialogWithLogout()
                            }

                            else -> {
                                showDialogWithAction(
                                    getString(R.string.error),
                                    getString(R.string.retry)
                                )
                            }
                        }
                    }

                    is SetTargetPriceEvent.FailurePriceUpdate -> {
                        when (event.errorType) {
                            ProductErrorState.PERMISSION_DENIED -> {
                                showDialogWithLogout()
                            }

                            else -> {
                                showDialogWithAction(
                                    getString(R.string.error_patch_price),
                                    getString(R.string.retry)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FragmentSetTargetPriceBinding.updateSlideValueWithPrice(targetPrice: Float) {
        val percent =
            ((targetPrice / setTargetPriceViewModel.state.value.productPrice) * MAX_PERCENT).toInt()
        val pricePercent = percent.coerceIn(MIN_PERCENT, MAX_PERCENT).roundAtFirstDigit()
        if (targetPrice > setTargetPriceViewModel.state.value.productPrice) {
            tvTargetPricePercent.text = getString(R.string.over_current_price)
        } else {
            tvTargetPricePercent.text =
                String.format(getString(R.string.current_price_percent), percent)
        }
        binding.tvTargetPricePercent.contentDescription = getString(
            R.string.target_price_percent_and_price,
            binding.tvTargetPricePercent.text,
            binding.tvSetPriceCurrentPrice.text
        )
        slTargetPrice.value = pricePercent.toFloat()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val MIN_PERCENT = 0
        const val MAX_PERCENT = 100
    }
}

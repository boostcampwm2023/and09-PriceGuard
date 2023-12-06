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
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.ui.showPermissionDeniedDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

        val productCode = arguments.getString("productCode") ?: ""
        val title = arguments.getString("productTitle") ?: ""
        val price = arguments.getInt("productPrice")

        setTargetPriceViewModel.updateTargetPrice((price * 0.8).toInt())

        binding.tvSetPriceCurrentPrice.text =
            String.format(
                resources.getString(R.string.won),
                NumberFormat.getNumberInstance().format(price)
            )

        setTargetPriceViewModel.setProductInfo(productCode, title, price)
        binding.etTargetPrice.setText((price * 0.8).toInt().toString())
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
            val targetPrice = if (it.toString().matches("^\\d+\$".toRegex())) {
                it.toString().toFloat()
            } else {
                0F
            }

            setTargetPriceViewModel.updateTargetPrice(targetPrice.toInt())

            val percent =
                ((targetPrice / setTargetPriceViewModel.state.value.productPrice) * MAX_PERCENT).toInt()

            binding.tvTargetPricePercent.text =
                String.format(getString(R.string.current_price_percent), percent)

            binding.updateSlideValueWithPrice(targetPrice, percent.roundAtFirstDigit())
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
                        showActivityFinishDialog(
                            getString(R.string.success_add),
                            getString(R.string.success_add_message)
                        )
                    }

                    is SetTargetPriceEvent.SuccessPriceUpdate -> {
                        showActivityFinishDialog(
                            getString(R.string.success_update),
                            getString(R.string.success_update_message)
                        )
                    }

                    is SetTargetPriceEvent.FailurePriceAdd -> {
                        when (event.errorType) {
                            ProductErrorState.EXIST -> {
                                showActivityFinishDialog(
                                    getString(R.string.error_add_product),
                                    getString(R.string.exist_product)
                                )
                            }

                            ProductErrorState.PERMISSION_DENIED -> {
                                requireActivity().showPermissionDeniedDialog(tokenRepository)
                            }

                            else -> {
                                showActivityFinishDialog(
                                    getString(R.string.error),
                                    getString(R.string.retry)
                                )
                            }
                        }
                    }

                    is SetTargetPriceEvent.FailurePriceUpdate -> {
                        when (event.errorType) {
                            ProductErrorState.PERMISSION_DENIED -> {
                                requireActivity().showPermissionDeniedDialog(tokenRepository)
                            }

                            else -> {
                                showActivityFinishDialog(
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

    private fun showActivityFinishDialog(title: String, message: String) {
        MaterialAlertDialogBuilder(requireActivity(), R.style.ThemeOverlay_App_MaterialAlertDialog)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.confirm) { _, _ -> requireActivity().finish() }
            .setOnDismissListener { requireActivity().finish() }
            .create()
            .show()
    }

    private fun FragmentSetTargetPriceBinding.updateSlideValueWithPrice(
        targetPrice: Float,
        percent: Int
    ) {
        val pricePercent = percent.coerceIn(MIN_PERCENT, MAX_PERCENT)
        if (targetPrice > setTargetPriceViewModel.state.value.productPrice) {
            tvTargetPricePercent.text = getString(R.string.over_current_price)
        }
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

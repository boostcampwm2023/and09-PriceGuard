package app.priceguard.ui.additem.setprice

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.data.repository.product.ProductErrorState
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.databinding.FragmentSetTargetPriceBinding
import app.priceguard.ui.additem.setprice.SetTargetPriceViewModel.SetTargetPriceEvent
import app.priceguard.ui.data.DialogConfirmAction
import app.priceguard.ui.slider.RoundSliderState
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.showDialogWithAction
import app.priceguard.ui.util.showDialogWithLogout
import dagger.hilt.android.AndroidEntryPoint
import java.text.NumberFormat
import javax.inject.Inject

@AndroidEntryPoint
class SetTargetPriceFragment : Fragment(), SetTargetPriceDialogFragment.OnDialogResultListener {

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
        initCollector()
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
        val targetPrice = arguments.getInt("productTargetPrice")

        setTargetPriceViewModel.setProductInfo(productCode, title, price, targetPrice)

        tvSetPriceCurrentPrice.text =
            String.format(
                resources.getString(R.string.won),
                NumberFormat.getNumberInstance().format(price)
            )
        tvSetPriceCurrentPrice.contentDescription =
            getString(R.string.current_price_info, tvSetPriceCurrentPrice.text)

        rsTargetPrice.setMaxPercentValue(MAX_PERCENT)
        rsTargetPrice.setStepSize(STEP_SIZE)

        btnTargetPriceDecrease.setOnClickListener {
            val sliderValue = if (rsTargetPrice.sliderValue >= STEP_SIZE) {
                rsTargetPrice.sliderValue - STEP_SIZE
            } else {
                0
            }
            rsTargetPrice.setValue(sliderValue)
            setTargetPriceViewModel.updateTargetPriceFromPercent(sliderValue)
        }

        btnTargetPriceIncrease.setOnClickListener {
            val sliderValue = if (rsTargetPrice.sliderValue <= MAX_PERCENT - STEP_SIZE) {
                rsTargetPrice.sliderValue + STEP_SIZE
            } else {
                MAX_PERCENT
            }
            rsTargetPrice.setValue(sliderValue)
            setTargetPriceViewModel.updateTargetPriceFromPercent(sliderValue)
        }

        calculatePercentAndSetSliderValue(price, targetPrice)
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

        rsTargetPrice.setSliderValueChangeListener { value ->
            if (setTargetPriceViewModel.state.value.isEnabledSliderListener) {
                setTargetPriceViewModel.updateTargetPriceFromPercent(value)
            }
        }

        tvTargetPriceContent.setOnClickListener {
            showConfirmationDialogForResult()
        }
    }

    private fun initCollector() {
        repeatOnStarted {
            setTargetPriceViewModel.state.collect { state ->
                if (state.targetPrice > state.productPrice) {
                    binding.rsTargetPrice.setSliderMode(RoundSliderState.ERROR)
                } else {
                    binding.rsTargetPrice.setSliderMode(RoundSliderState.ACTIVE)
                }
            }
        }
    }

    private fun calculatePercentAndSetSliderValue(productPrice: Int, targetPrice: Int) {
        setTargetPriceViewModel.setSliderChangeListenerEnabled(false)
        binding.rsTargetPrice.setValue((targetPrice.toFloat() / productPrice.toFloat() * 100F).toInt())
        setTargetPriceViewModel.setSliderChangeListenerEnabled(true)
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

    private fun showConfirmationDialogForResult() {
        val tag = "set_target_price_dialog_fragment_from_fragment"
        if (requireActivity().supportFragmentManager.findFragmentByTag(tag) != null) return

        val dialogFragment = SetTargetPriceDialogFragment()
        val bundle = Bundle()
        bundle.putString("title", getString(R.string.set_target_price_dialog_title))
        bundle.putInt("price", setTargetPriceViewModel.state.value.targetPrice)
        dialogFragment.setOnDialogResultListener(this)
        dialogFragment.arguments = bundle
        dialogFragment.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDialogResult(result: Int) {
        setTargetPriceViewModel.updateTargetPrice(result)
        calculatePercentAndSetSliderValue(setTargetPriceViewModel.state.value.productPrice, result)
    }

    companion object {
        const val STEP_SIZE = 10
        const val MAX_PERCENT = 200
    }
}

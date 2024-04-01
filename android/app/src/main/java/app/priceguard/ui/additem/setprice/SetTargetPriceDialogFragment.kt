package app.priceguard.ui.additem.setprice

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import app.priceguard.R
import app.priceguard.databinding.FragmentTargetPriceDialogBinding
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.setTextColorWithEnabled
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SetTargetPriceDialogFragment : DialogFragment() {

    private var _binding: FragmentTargetPriceDialogBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SetTargetPriceDialogViewModel by viewModels()

    private var resultListener: OnDialogResultListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentTargetPriceDialogBinding.inflate(requireActivity().layoutInflater)
        val view = binding.root

        val title = arguments?.getString("title") ?: ""

        val dialogBuilder = MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.ThemeOverlay_App_MaterialAlertDialog
        ).apply {
            setTitle(title)
            setView(view)
            setNegativeButton(R.string.cancel) { _, _ -> dismiss() }
            setPositiveButton(R.string.confirm) { _, _ ->
                resultListener?.onDialogResult(viewModel.state.value.targetPrice.toInt())
                dismiss()
            }
        }
        val dialog = dialogBuilder.create()

        repeatOnStarted {
            viewModel.state.collect { state ->
                viewModel.updateTextChangedEnabled(false)
                binding.etTargetPriceDialog.setText(
                    getString(R.string.won, getString(R.string.comma_number, state.targetPrice))
                )
                val positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE)
                positiveButton.isEnabled = !state.isErrorMessageVisible
                positiveButton.setTextColorWithEnabled()

                viewModel.updateTextChangedEnabled(true)
            }
        }

        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initListener()

        val price = arguments?.getInt("price") ?: 0
        viewModel.updateTargetPrice(price.toLong())

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initListener() {
        binding.etTargetPriceDialog.addTextChangedListener {
            binding.etTargetPriceDialog.setSelection(it.toString().length - 1)

            val price = extractAndConvertToInteger(it.toString())

            if (viewModel.state.value.isTextChanged) {
                if (price > MAX_TARGET_PRICE) {
                    viewModel.updateErrorMessageVisible(true)
                } else {
                    viewModel.updateErrorMessageVisible(false)
                }
                viewModel.updateTargetPrice(price)
            }
        }

        binding.etTargetPriceDialog.setOnClickListener {
            binding.etTargetPriceDialog.setSelection(binding.etTargetPriceDialog.text.toString().length - 1)
        }
    }

    private fun extractAndConvertToInteger(text: String): Long {
        val digits = text.filter { it.isDigit() }
        return (digits.toLongOrNull() ?: 0).coerceIn(0, MAX_TARGET_PRICE * 10 - 1)
    }

    fun setOnDialogResultListener(listener: OnDialogResultListener) {
        resultListener = listener
    }

    interface OnDialogResultListener {
        fun onDialogResult(result: Int)
    }

    companion object {
        const val MAX_TARGET_PRICE = 1_000_000_000L
    }
}

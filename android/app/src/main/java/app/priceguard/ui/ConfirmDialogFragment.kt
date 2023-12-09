package app.priceguard.ui

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import app.priceguard.R
import app.priceguard.ui.data.DialogConfirmAction
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ConfirmDialogFragment : DialogFragment() {

    private lateinit var title: String
    private lateinit var message: String
    private lateinit var action: DialogConfirmAction
    private lateinit var negativeButtonText: String

    private var resultListener: OnDialogResultListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        initDialogInfo()

        return MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.ThemeOverlay_App_MaterialAlertDialog
        ).apply {
            setTitle(title)
            setMessage(message)
            setNegativeButton(negativeButtonText) { _, _ -> }
            setPositiveButton(getString(R.string.confirm)) { _, _ ->
                when (action) {
                    DialogConfirmAction.FINISH -> {
                        requireActivity().finish()
                    }

                    DialogConfirmAction.CUSTOM -> {
                        resultListener?.onDialogResult(true)
                    }

                    DialogConfirmAction.NOTHING -> {}
                }
                dismiss()
            }
        }.create()
    }

    private fun initDialogInfo() {
        title = arguments?.getString("title") ?: ""
        message = arguments?.getString("message") ?: ""

        val actionString = arguments?.getString("actionString")
        actionString?.let { actionString ->
            action = DialogConfirmAction.valueOf(actionString)
        }?.run {
            DialogConfirmAction.NOTHING
        }

        negativeButtonText = if (action == DialogConfirmAction.CUSTOM) {
            getString(R.string.cancel)
        } else {
            ""
        }
    }

    override fun onStart() {
        super.onStart()

        if (action == DialogConfirmAction.NOTHING || action == DialogConfirmAction.CUSTOM) {
            dialog?.setCancelable(true)
        } else {
            dialog?.setCancelable(false)
        }
    }

    interface OnDialogResultListener {
        fun onDialogResult(result: Boolean)
    }

    fun setOnDialogResultListener(listener: OnDialogResultListener) {
        resultListener = listener
    }
}

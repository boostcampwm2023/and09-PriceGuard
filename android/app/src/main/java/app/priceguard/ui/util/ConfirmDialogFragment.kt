package app.priceguard.ui.util

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import app.priceguard.R
import app.priceguard.ui.data.DialogConfirmAction
import app.priceguard.ui.home.HomeActivity
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
                        resultListener?.onDialogResult(getRequestCode(), true)
                    }

                    DialogConfirmAction.HOME -> {
                        goToHomeActivity()
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

    private fun goToHomeActivity() {
        val activityIntent = requireActivity().intent
        if (activityIntent?.action == Intent.ACTION_SEND) {
            val intent = Intent(requireActivity(), HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        requireActivity().finish()
    }

    private fun getRequestCode(): Int {
        return arguments?.getInt("requestCode") ?: 0
    }

    override fun onStart() {
        super.onStart()

        if (action == DialogConfirmAction.NOTHING || action == DialogConfirmAction.CUSTOM) {
            dialog?.setCancelable(true)
        } else {
            dialog?.setCancelable(false)
        }
    }

    fun setOnDialogResultListener(listener: OnDialogResultListener) {
        resultListener = listener
    }

    interface OnDialogResultListener {
        fun onDialogResult(requestCode: Int, result: Boolean)
    }
}

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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        title = arguments?.getString("title") ?: ""
        message = arguments?.getString("message") ?: ""

        val actionString = arguments?.getString("actionString")
        actionString?.let { actionString ->
            action = DialogConfirmAction.valueOf(actionString)
        }?.run {
            DialogConfirmAction.NOTHING
        }

        return MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.ThemeOverlay_App_MaterialAlertDialog
        ).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(getString(R.string.confirm)) { _, _ ->
                when (action) {
                    DialogConfirmAction.FINISH -> requireActivity().finish()
                    DialogConfirmAction.NOTHING -> {}
                }
                dismiss()
            }
        }.create()
    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false)
    }
}

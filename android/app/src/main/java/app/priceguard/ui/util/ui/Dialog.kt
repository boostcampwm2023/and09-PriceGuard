package app.priceguard.ui.util.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import app.priceguard.ui.ConfirmDialogFragment
import app.priceguard.ui.data.DialogConfirmAction

fun AppCompatActivity.showConfirmDialog(
    title: String,
    message: String,
    action: DialogConfirmAction = DialogConfirmAction.NOTHING
) {
    val dialogFragment = ConfirmDialogFragment()
    val bundle = Bundle()
    bundle.putString("title", title)
    bundle.putString("message", message)
    bundle.putString("actionString", action.name)
    dialogFragment.arguments = bundle
    dialogFragment.show(supportFragmentManager, "confirm_dialog_fragment_from_activity")
}

fun Fragment.showDialogWithAction(
    title: String,
    message: String,
    action: DialogConfirmAction = DialogConfirmAction.NOTHING
) {
    val dialogFragment = ConfirmDialogFragment()
    val bundle = Bundle()
    bundle.putString("title", title)
    bundle.putString("message", message)
    bundle.putString("actionString", action.name)
    dialogFragment.arguments = bundle
    dialogFragment.show(requireActivity().supportFragmentManager, "confirm_dialog_fragment_from_fragment")
}

fun AppCompatActivity.showDialogWithLogout() {
    val dialogFragment = ConfirmDialogFragment()
    dialogFragment.show(supportFragmentManager, "error_dialog_fragment_from_activity")
}

fun Fragment.showDialogWithLogout() {
    val dialogFragment = ConfirmDialogFragment()
    dialogFragment.show(requireActivity().supportFragmentManager, "error_dialog_fragment_from_fragment")
}

package app.priceguard.ui.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import app.priceguard.ui.data.DialogConfirmAction

fun AppCompatActivity.showConfirmDialog(
    title: String,
    message: String,
    action: DialogConfirmAction = DialogConfirmAction.NOTHING
) {
    val tag = "confirm_dialog_fragment_from_activity"
    if (supportFragmentManager.findFragmentByTag(tag) != null) return

    val dialogFragment = ConfirmDialogFragment()
    val bundle = Bundle()
    bundle.putString("title", title)
    bundle.putString("message", message)
    bundle.putString("actionString", action.name)
    dialogFragment.arguments = bundle
    dialogFragment.show(supportFragmentManager, tag)
}

fun Fragment.showDialogWithAction(
    title: String,
    message: String,
    action: DialogConfirmAction = DialogConfirmAction.NOTHING
) {
    val tag = "confirm_dialog_fragment_from_fragment"
    if (requireActivity().supportFragmentManager.findFragmentByTag(tag) != null) return

    val dialogFragment = ConfirmDialogFragment()
    val bundle = Bundle()
    bundle.putString("title", title)
    bundle.putString("message", message)
    bundle.putString("actionString", action.name)
    dialogFragment.arguments = bundle
    dialogFragment.show(requireActivity().supportFragmentManager, tag)
}

fun AppCompatActivity.showDialogWithAction(
    title: String,
    message: String,
    action: DialogConfirmAction = DialogConfirmAction.NOTHING
) {
    val tag = "confirm_dialog_fragment_with_action_from_activity"
    if (supportFragmentManager.findFragmentByTag(tag) != null) return

    val dialogFragment = ConfirmDialogFragment()
    val bundle = Bundle()
    bundle.putString("title", title)
    bundle.putString("message", message)
    bundle.putString("actionString", action.name)
    dialogFragment.arguments = bundle
    dialogFragment.show(supportFragmentManager, tag)
}

fun AppCompatActivity.showDialogWithLogout() {
    val tag = "error_dialog_fragment_from_activity"
    if (supportFragmentManager.findFragmentByTag(tag) != null) return

    val dialogFragment = ErrorDialogFragment()
    dialogFragment.show(supportFragmentManager, tag)
}

fun Fragment.showDialogWithLogout() {
    val tag = "error_dialog_fragment_from_fragment"
    if (requireActivity().supportFragmentManager.findFragmentByTag(tag) != null) return

    val dialogFragment = ErrorDialogFragment()
    dialogFragment.show(requireActivity().supportFragmentManager, tag)
}

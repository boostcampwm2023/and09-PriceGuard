package app.priceguard.ui

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import app.priceguard.R
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ErrorDialogFragment : DialogFragment() {

    @Inject
    lateinit var tokenRepository: TokenRepository

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.ThemeOverlay_App_MaterialAlertDialog
        ).apply {
            setTitle(getString(R.string.permission_denied_title))
            setMessage(getString(R.string.permission_denied_message))
            setPositiveButton(getString(R.string.confirm)) { _, _ -> goBackToLoginActivity(tokenRepository) }
        }.create()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        goBackToLoginActivity(tokenRepository)
    }

    private fun goBackToLoginActivity(tokenRepository: TokenRepository) {
        CoroutineScope(Dispatchers.IO).launch { tokenRepository.clearTokens() }
        val intent = Intent(requireActivity(), LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }
}

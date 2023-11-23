package app.priceguard.ui.util.ui

import android.app.Activity
import android.content.Intent
import app.priceguard.R
import app.priceguard.data.repository.TokenRepository
import app.priceguard.ui.login.LoginActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Activity.showPermissionDeniedDialog(tokenRepository: TokenRepository) {
    MaterialAlertDialogBuilder(this, R.style.ThemeOverlay_App_MaterialAlertDialog)
        .setTitle(getString(R.string.permission_denied_title))
        .setMessage(getString(R.string.permission_denied_message))
        .setPositiveButton(getString(R.string.confirm)) { _, _ -> goBackToLoginActivity(tokenRepository) }
        .setOnDismissListener { goBackToLoginActivity(tokenRepository) }
        .create()
        .show()
}

fun Activity.goBackToLoginActivity(tokenRepository: TokenRepository) {
    CoroutineScope(Dispatchers.IO).launch { tokenRepository.clearTokens() }
    val intent = Intent(this, LoginActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    startActivity(intent)
    finish()
}

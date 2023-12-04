package app.priceguard.ui.util.ui

import android.content.Context
import android.content.Intent
import android.provider.Settings

fun Context.openNotificationSettings() {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .putExtra(Settings.EXTRA_APP_PACKAGE, this.packageName)
    startActivity(intent)
}

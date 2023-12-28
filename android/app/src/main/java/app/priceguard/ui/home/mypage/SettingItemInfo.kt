package app.priceguard.ui.home.mypage

import android.graphics.drawable.Drawable

data class SettingItemInfo(
    val id: Setting,
    val icon: Drawable?,
    val title: String
)

enum class Setting {
    NOTIFICATION,
    THEME,
    LICENSE,
    LOGOUT,
    DELETE_ACCOUNT
}

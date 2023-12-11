package app.priceguard.ui.util

import android.app.Activity
import com.google.android.material.color.MaterialColors

fun Activity.applySystemNavigationBarColor(colorType: SystemNavigationColorState) {
    window.navigationBarColor = MaterialColors.getColor(
        this,
        colorType.colorType,
        getColor(android.R.color.transparent)
    )
}

enum class SystemNavigationColorState(val colorType: Int) {
    SURFACE(com.google.android.material.R.attr.colorSurface),
    BOTTOM_NAVIGATION(com.google.android.material.R.attr.colorSurfaceContainerHigh)
}

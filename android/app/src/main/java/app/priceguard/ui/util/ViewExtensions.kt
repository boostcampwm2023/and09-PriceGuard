package app.priceguard.ui.util

import android.util.TypedValue
import android.widget.Button

fun Button.setTextColorWithEnabled() {
    val value = TypedValue()

    val color = if (!isEnabled) {
        com.google.android.material.R.attr.colorOutline
    } else {
        com.google.android.material.R.attr.colorPrimary
    }
    context?.theme?.resolveAttribute(color, value, true)
    setTextColor(value.data)
}

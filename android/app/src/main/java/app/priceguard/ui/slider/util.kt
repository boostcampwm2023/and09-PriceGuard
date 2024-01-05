package app.priceguard.ui.slider

import android.content.Context

data class Px(val value: Float)

data class Dp(val value: Float)

fun Px.toDp(context: Context): Dp {
    val density = context.resources.displayMetrics.density
    return Dp(value / density)
}

fun Dp.toPx(context: Context): Px {
    val density = context.resources.displayMetrics.density
    return Px(value * density)
}

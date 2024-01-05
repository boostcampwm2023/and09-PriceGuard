package app.priceguard.ui.slider

import android.content.Context
import android.util.TypedValue

data class Px(val value: Float)

data class Dp(val value: Float)

data class Sp(val value: Float)

fun Px.toDp(context: Context): Dp {
    val density = context.resources.displayMetrics.density
    return Dp(value / density)
}

fun Dp.toPx(context: Context): Px {
    val density = context.resources.displayMetrics.density
    return Px(value * density)
}

fun Sp.toPx(context: Context): Px {
    return Px(
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            value,
            context.resources.displayMetrics
        )
    )
}

fun Float.toRadian(): Float {
    return this * Math.PI.toFloat() / 180F
}

fun Float.toDegree(): Float {
    return this * 180F / Math.PI.toFloat()
}

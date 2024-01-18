package app.priceguard.ui.slider

import android.content.Context
import android.util.TypedValue

data class Px(val value: Float)

data class Dp(val value: Float)

data class Sp(val value: Float)

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

fun Double.toRadian(): Double {
    return this * Math.PI / 180
}

fun Float.toRadian(): Float {
    return this * Math.PI.toFloat() / 180F
}

fun Int.toRadian(): Double {
    return this * Math.PI / 180
}

fun Double.toDegree(): Double {
    return this * 180 / Math.PI
}

fun Float.toDegree(): Float {
    return this * 180F / Math.PI.toFloat()
}

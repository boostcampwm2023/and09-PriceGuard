package app.priceguard.ui.slider

import android.content.Context

data class Px(val value: Float)

data class Dp(val value: Float)

fun Px.toDp(context: Context): Dp {
    val density = context.resources.displayMetrics.density
    return Dp(value / density)
}

operator fun Px.plus(other: Px): Px {
    return Px(value + other.value)
}

operator fun Px.minus(other: Px): Px {
    return Px(value - other.value)
}

operator fun Px.times(other: Px): Px {
    return Px(value * other.value)
}

operator fun Px.div(other: Px): Px {
    return Px(value / other.value)
}

operator fun Px.rem(other: Px): Px {
    return Px(value % other.value)
}

fun Dp.toPx(context: Context): Px {
    val density = context.resources.displayMetrics.density
    return Px(value * density)
}

operator fun Dp.plus(other: Dp): Dp {
    return Dp(value + other.value)
}

operator fun Dp.minus(other: Dp): Dp {
    return Dp(value - other.value)
}

operator fun Dp.times(other: Dp): Dp {
    return Dp(value * other.value)
}

operator fun Dp.div(other: Dp): Dp {
    return Dp(value / other.value)
}

operator fun Dp.rem(other: Dp): Dp {
    return Dp(value % other.value)
}

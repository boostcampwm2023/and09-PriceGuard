package app.priceguard.ui.util.drawable

import android.content.Context
import app.priceguard.R
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable

fun getCircularProgressIndicatorDrawable(context: Context, style: Int = R.style.Theme_PriceGuard_CircularProgressIndicator): IndeterminateDrawable<CircularProgressIndicatorSpec> {
    val spec = CircularProgressIndicatorSpec(
        context,
        null,
        0,
        style
    )

    return IndeterminateDrawable.createCircularDrawable(context, spec)
}

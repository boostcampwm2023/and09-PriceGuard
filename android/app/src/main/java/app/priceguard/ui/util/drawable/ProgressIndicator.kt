package app.priceguard.ui.util.drawable

import android.content.Context
import app.priceguard.R
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable

fun getCircularProgressIndicatorDrawable(context: Context): IndeterminateDrawable<CircularProgressIndicatorSpec> {
    val spec = CircularProgressIndicatorSpec(
        context,
        null,
        0,
        R.style.Theme_PriceGuard_CircularProgressIndicator
    )

    return IndeterminateDrawable.createCircularDrawable(context, spec)
}

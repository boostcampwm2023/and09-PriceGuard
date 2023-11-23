package app.priceguard.ui.util.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import app.priceguard.R
import java.text.DecimalFormat

@BindingAdapter("priceText")
fun TextView.toPriceString(price: Int?) {
    if (price == null) text = ""
    val priceFormat = DecimalFormat("#,###")
    text = String.format(resources.getString(R.string.won), priceFormat.format(price))
}

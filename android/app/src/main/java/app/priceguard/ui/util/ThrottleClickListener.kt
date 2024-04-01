package app.priceguard.ui.util

import android.view.View
import android.widget.Button
import androidx.databinding.BindingAdapter

class OnThrottleClickListener(
    private val onClickListener: View.OnClickListener,
    private val interval: Long = 500L
) : View.OnClickListener {

    private var clickable = true

    override fun onClick(v: View?) {
        if (clickable) {
            clickable = false
            v?.run {
                postDelayed({
                    clickable = true
                }, interval)
                onClickListener.onClick(v)
            }
        }
    }
}

fun View.onThrottleClick(action: (v: View) -> Unit) {
    val listener = View.OnClickListener { action(it) }
    setOnClickListener(OnThrottleClickListener(listener))
}

@BindingAdapter("onThrottleClick")
fun Button.bindThrottleClick(action: () -> Unit) {
    val listener = View.OnClickListener { action() }
    setOnClickListener(OnThrottleClickListener(listener))
}

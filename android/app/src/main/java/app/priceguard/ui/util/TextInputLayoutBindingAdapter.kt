package app.priceguard.ui.util

import androidx.databinding.BindingAdapter
import com.google.android.material.textfield.TextInputLayout

@BindingAdapter("textInputLayoutError")
fun TextInputLayout.bindTextInputLayoutError(errorMessage: String?) {
    this.error = errorMessage
}

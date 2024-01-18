package app.priceguard.ui.additem.setprice

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SetTargetPriceDialogViewModel : ViewModel() {

    data class SetTargetPriceDialogState(
        val targetPrice: Long = 0,
        val isTextChanged: Boolean = false,
        val isErrorMessageVisible: Boolean = false
    )

    private val _state = MutableStateFlow(SetTargetPriceDialogState())
    val state = _state.asStateFlow()

    fun updateTargetPrice(price: Long) {
        _state.value = _state.value.copy(targetPrice = price)
    }

    fun updateTextChangedEnabled(isEnabled: Boolean) {
        _state.value = _state.value.copy(isTextChanged = isEnabled)
    }

    fun updateErrorMessageVisible(isEnabled: Boolean) {
        _state.value = _state.value.copy(isErrorMessageVisible = isEnabled)
    }
}

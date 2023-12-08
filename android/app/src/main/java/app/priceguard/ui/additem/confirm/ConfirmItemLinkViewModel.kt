package app.priceguard.ui.additem.confirm

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConfirmItemLinkViewModel : ViewModel() {

    data class ConfirmItemLinkUIState(
        val price: Int? = null,
        val brand: String? = null,
        val name: String? = null,
        val imageUrl: String? = null
    )

    private var _state: MutableStateFlow<ConfirmItemLinkUIState> =
        MutableStateFlow(ConfirmItemLinkUIState())
    val state: StateFlow<ConfirmItemLinkUIState> = _state.asStateFlow()

    fun setUIState(price: Int, brand: String, name: String, imageUrl: String) {
        _state.update {
            it.copy(price = price, brand = brand, name = name, imageUrl = imageUrl)
        }
    }
}

package app.priceguard.ui.additem.confirm

import androidx.lifecycle.ViewModel
import app.priceguard.data.dto.ProductVerifyDTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfirmItemLinkViewModel : ViewModel() {

    private val _flow = MutableStateFlow(ProductVerifyDTO("", "", 0, "", ""))
    val flow = _flow.asStateFlow()

    fun setProductInfo(productInfo: ProductVerifyDTO) {
        _flow.value = productInfo
    }
}

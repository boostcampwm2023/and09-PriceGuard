package app.priceguard.ui.additem

import androidx.lifecycle.ViewModel
import app.priceguard.data.dto.ProductInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfirmItemLinkViewModel : ViewModel() {

    private val _flow = MutableStateFlow(
        // 더미 데이터 추후 제거 필요함
        ProductInfo(
            title = "[농심]신라면 멀티팩 40봉",
            brand = "롯데아이몰",
            price = "31,670원"
        )
    )
    val flow = _flow.asStateFlow()
}

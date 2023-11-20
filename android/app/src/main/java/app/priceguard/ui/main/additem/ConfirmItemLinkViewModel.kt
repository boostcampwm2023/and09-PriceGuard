package app.priceguard.ui.main.additem

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConfirmItemLinkViewModel : ViewModel() {
    data class ProductInfo(
        val title: String = "",
        val brand: String = "",
        val logo: Int = 0,
        val price: String = ""
    )

    private val _flow = MutableStateFlow(
        ProductInfo(
            title = "[농심]신라면 멀티팩 40봉",
            brand = "롯데아이몰",
            price = "31,670원"
        )
    )
    val flow = _flow.asStateFlow()
}

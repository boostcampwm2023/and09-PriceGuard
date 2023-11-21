package app.priceguard.ui.home.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.ui.home.ProductSummary.UserProductSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductListViewModel @Inject constructor() : ViewModel() {

    private var _list = MutableStateFlow<List<UserProductSummary>>(listOf())
    val list: StateFlow<List<UserProductSummary>> = _list.asStateFlow()

    init {
        viewModelScope.launch {
            getProductList()
        }
    }

    fun getProductList() {
        // TODO: repository 구현 후 연결
        _list.value = listOf(
            UserProductSummary(
                "11번가",
                "오뚜기 진라면, 120g, 40개",
                "28080원",
                "-12.6%",
                false
            ),
            UserProductSummary(
                "11번가",
                "오뚜기 진라면, 120g, 40개",
                "28080원",
                "-12.6%",
                false
            )
        )
    }
}

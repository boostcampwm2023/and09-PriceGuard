package app.priceguard.ui.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.ui.home.ProductSummary.RecommendedProductSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RecommendedProductViewModel @Inject constructor() : ViewModel() {

    private var _recommendedProductList = MutableStateFlow<List<RecommendedProductSummary>>(listOf())
    val recommendedProductList: StateFlow<List<RecommendedProductSummary>> = _recommendedProductList.asStateFlow()

    init {
        viewModelScope.launch {
            getProductList()
        }
    }

    fun getProductList() {
        // TODO: repository 구현 후 연결
        _recommendedProductList.value = listOf(
            RecommendedProductSummary(
                "11번가",
                "오뚜기 진라면, 120g, 40개",
                "28080원",
                "-12.6%",
                1
            ),
            RecommendedProductSummary(
                "11번가",
                "오뚜기 진라면, 120g, 40개",
                "28080원",
                "-12.6%",
                2
            )
        )
    }
}

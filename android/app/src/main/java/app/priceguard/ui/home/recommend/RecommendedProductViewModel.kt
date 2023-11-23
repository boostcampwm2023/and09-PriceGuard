package app.priceguard.ui.home.recommend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.ProductRepository
import app.priceguard.ui.home.ProductSummary.RecommendedProductSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class RecommendedProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private var _recommendedProductList =
        MutableStateFlow<List<RecommendedProductSummary>>(listOf())
    val recommendedProductList: StateFlow<List<RecommendedProductSummary>> =
        _recommendedProductList.asStateFlow()

    init {
        viewModelScope.launch {
            getProductList()
        }
    }

    fun getProductList() {
        viewModelScope.launch {
            val list = productRepository.getProductList()
            _recommendedProductList.value = list.trackingList.map { data ->
                RecommendedProductSummary(
                    data.shop,
                    data.productName,
                    "",
                    "",
                    data.productCode,
                    1
                )
            }
        }
    }
}

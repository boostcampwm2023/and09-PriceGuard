package app.priceguard.ui.home.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.dto.ErrorState
import app.priceguard.data.network.RepositoryResult
import app.priceguard.data.repository.ProductRepository
import app.priceguard.ui.home.ProductSummary.UserProductSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    sealed class ProductListEvent {
        data object PermissionDenied : ProductListEvent()
    }

    private var _isRefreshing: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var _productList = MutableStateFlow<List<UserProductSummary>>(listOf())
    val productList: StateFlow<List<UserProductSummary>> = _productList.asStateFlow()

    private var _events = MutableSharedFlow<ProductListEvent>()
    val events: SharedFlow<ProductListEvent> = _events.asSharedFlow()

    init {
        getProductList(false)
    }

    fun getProductList(isRefresh: Boolean) {
        viewModelScope.launch {
            if (isRefresh) {
                _isRefreshing.value = true
            }

            val result = productRepository.getProductList()

            _isRefreshing.value = false

            when (result) {
                is RepositoryResult.Success -> {
                    _productList.value = result.data.map { data ->
                        UserProductSummary(
                            data.shop,
                            data.productName,
                            data.price.toString(),
                            "-15.0%",
                            data.productCode,
                            true
                        )
                    }
                }

                is RepositoryResult.Error -> {
                    when (result.errorState) {
                        ErrorState.PERMISSION_DENIED -> {
                            _events.emit(ProductListEvent.PermissionDenied)
                        }

                        else -> {
                        }
                    }
                }
            }
        }
    }
}

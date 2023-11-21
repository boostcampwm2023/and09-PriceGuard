package app.priceguard.ui.home.recommend

import android.util.Log
import androidx.lifecycle.ViewModel
import app.priceguard.ui.home.ProductSummary
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class RecommendedProductViewModel @Inject constructor() : ViewModel() {

    private var _list = MutableStateFlow<List<ProductSummary>>(listOf())
    val list: StateFlow<List<ProductSummary>> = _list.asStateFlow()

    fun refreshScreen() {
        // TODO
        Log.d("TEST", "refresh")
    }
}

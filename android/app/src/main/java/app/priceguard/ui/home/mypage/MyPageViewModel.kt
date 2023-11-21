package app.priceguard.ui.home.mypage

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyPageViewModel : ViewModel() {

    // 더미 데이터
    data class MyPageInfo(
        val name: String = "박승준 님",
        val email: String = "aaa@aa.aa",
        val firstName: String = name.first().toString()
    )

    private val _flow = MutableStateFlow(MyPageInfo())
    val flow = _flow.asStateFlow()
}

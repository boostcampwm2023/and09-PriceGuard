package app.priceguard.ui.intro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class IntroViewModel : ViewModel() {
    private val _eventFlow = MutableSharedFlow<Event>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun startLoginActivity() {
        event(Event.StartLoginActivity)
    }

    fun startSignupActivity() {
        event(Event.StartSignupActivity)
    }

    private fun event(event: Event) {
        viewModelScope.launch {
            _eventFlow.emit(event)
        }
    }

    sealed class Event {

        object StartLoginActivity : Event()

        object StartSignupActivity : Event()
    }
}

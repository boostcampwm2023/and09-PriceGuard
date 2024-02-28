package app.priceguard.ui.login.findpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.priceguard.data.repository.RepositoryResult
import app.priceguard.data.repository.auth.AuthErrorState
import app.priceguard.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    data class ResetPasswordState(
        val password: String = "",
        val passwordConfirm: String = "",
        val verifyToken: String = "",
        val isMatchedPasswordRegex: Boolean = false,
        val isMatchedPasswordConfirm: Boolean = false
    )

    sealed class ResetPasswordEvent {
        data object SuccessResetPassword : ResetPasswordEvent()
        data object ErrorVerifyToken : ResetPasswordEvent()
        data object UndefinedError : ResetPasswordEvent()
    }

    private val _state = MutableStateFlow(ResetPasswordState())
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<ResetPasswordEvent>()
    val event = _event.asSharedFlow()

    fun resetPassword() {
        viewModelScope.launch {
            val response =
                authRepository.resetPassword(_state.value.password, _state.value.verifyToken)
            when (response) {
                is RepositoryResult.Error -> {
                    when (response.errorState) {
                        AuthErrorState.UNAUTHORIZED, AuthErrorState.EXPIRE -> {
                            _event.emit(ResetPasswordEvent.ErrorVerifyToken)
                        }

                        else -> {
                            _event.emit(ResetPasswordEvent.UndefinedError)
                        }
                    }
                }

                is RepositoryResult.Success -> {
                    _event.emit(ResetPasswordEvent.SuccessResetPassword)
                }
            }
        }
    }

    fun updatePassword(password: String) {
        _state.value = _state.value.copy(password = password)
        checkPasswordRegex(password)
        checkMatchPassword()
    }

    fun updatePasswordConfirm(passwordConfirm: String) {
        _state.value = _state.value.copy(passwordConfirm = passwordConfirm)
        checkMatchPassword()
    }

    private fun checkPasswordRegex(password: String) {
        val passwordPattern =
            """^(?=[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]*\d)(?=[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]*[a-z])(?=[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]*[A-Z])(?=[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]*[!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/])[A-Za-z\d!@#$%^&*()_+={};:'"~`,.?<>|\-\[\]\\/]{8,16}$""".toRegex()

        _state.value = _state.value.copy(
            isMatchedPasswordRegex = password.matches(passwordPattern)
        )
    }

    private fun checkMatchPassword() {
        _state.value = _state.value.copy(
            isMatchedPasswordConfirm = _state.value.password == _state.value.passwordConfirm
        )
    }
}

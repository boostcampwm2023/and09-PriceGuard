package app.priceguard.ui.data

import app.priceguard.data.dto.login.LoginState

data class LoginResult(
    val loginState: LoginState,
    val accessToken: String?,
    val refreshToken: String?
)

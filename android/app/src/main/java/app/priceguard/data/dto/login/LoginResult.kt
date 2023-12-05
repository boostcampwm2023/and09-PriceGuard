package app.priceguard.data.dto.login

data class LoginResult(
    val loginState: LoginState,
    val accessToken: String?,
    val refreshToken: String?
)

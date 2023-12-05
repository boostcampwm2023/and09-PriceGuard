package app.priceguard.data.dto.signup

data class SignupResult(
    val signUpState: SignupState,
    val accessToken: String?,
    val refreshToken: String?
)

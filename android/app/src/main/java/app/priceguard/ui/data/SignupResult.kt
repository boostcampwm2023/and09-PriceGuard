package app.priceguard.ui.data

import app.priceguard.data.dto.signup.SignupState

data class SignupResult(
    val signUpState: SignupState,
    val accessToken: String?,
    val refreshToken: String?
)

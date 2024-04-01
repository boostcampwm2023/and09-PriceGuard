package app.priceguard.data.repository.auth

enum class AuthErrorState {
    INVALID_REQUEST,
    UNAUTHORIZED,
    DUPLICATED_EMAIL,
    NOT_FOUND,
    EXPIRE,
    OVER_LIMIT,
    UNDEFINED_ERROR
}

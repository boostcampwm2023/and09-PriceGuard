package app.priceguard.data.repository.token

enum class TokenErrorState {
    INVALID_REQUEST,
    UNAUTHORIZED,
    EXPIRED,
    NOT_FOUND,
    UNDEFINED_ERROR
}

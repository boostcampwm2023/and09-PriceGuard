package app.priceguard.data.repository.product

enum class ProductErrorState {
    PERMISSION_DENIED,
    INVALID_REQUEST,
    NOT_FOUND,
    EXIST,
    FULL_STORAGE,
    UNDEFINED_ERROR
}

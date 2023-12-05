package app.priceguard.data.dto.recommend

import kotlinx.serialization.Serializable

@Serializable
data class RecommendProductResponse(
    val statusCode: Int,
    val message: String,
    val recommendList: List<RecommendProductDTO>? = null
)

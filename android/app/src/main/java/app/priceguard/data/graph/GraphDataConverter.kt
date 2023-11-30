package app.priceguard.data.graph

import app.priceguard.data.dto.PriceDataDTO
import javax.inject.Inject

class GraphDataConverter @Inject constructor() {

    fun toDataset(priceData: List<PriceDataDTO>?, period: Int = 5): List<ProductChartData> {
        priceData ?: return listOf()
        if (priceData.isEmpty()) {
            return listOf()
        }
        var currentTime = 0
        var dataList = mutableListOf<ProductChartData>()
        priceData.forEachIndexed { i, dto ->
            dto.time ?: return@forEachIndexed
            dto.price ?: return@forEachIndexed
            dto.isSoldOut ?: return@forEachIndexed
            if (i == 0) {
                currentTime = dto.time.milliSecondToMinute()
                dataList.add(ProductChartData(currentTime.toFloat(), dto.price.toFloat(), dto.isSoldOut.not()))
            } else {
                currentTime += period
                while (currentTime < dto.time.milliSecondToMinute()) {
                    dataList.add(ProductChartData(currentTime.toFloat(), dto.price.toFloat(), dto.isSoldOut.not()))
                    currentTime += period
                }
            }
        }
        return dataList.toList()
    }

    private fun Float.milliSecondToMinute(): Int {
        return this.toInt() / 1000
    }
}

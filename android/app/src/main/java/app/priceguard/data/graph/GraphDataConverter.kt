package app.priceguard.data.graph

import app.priceguard.data.dto.PriceDataDTO
import javax.inject.Inject

class GraphDataConverter @Inject constructor() {

    fun toDataset(priceData: List<PriceDataDTO>?, period: Int = 5): List<ProductChartData> {
        priceData ?: return listOf()
        if (priceData.isEmpty()) {
            return listOf()
        }
        val dataList = mutableListOf<ProductChartData>()
        priceData.forEach { dto ->
            dto.time ?: return@forEach
            dto.price ?: return@forEach
            dto.isSoldOut ?: return@forEach
            dataList.add(ProductChartData(dto.time / 1000, dto.price.toFloat(), dto.isSoldOut.not()))
        }
        dataList.add(ProductChartData((System.currentTimeMillis() / 1000).toFloat(), dataList.last().y, dataList.last().valid))
        return dataList.toList()
    }
}

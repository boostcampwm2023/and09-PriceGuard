package app.priceguard.data.graph

import app.priceguard.data.dto.PriceDataDTO

class GraphDataConverter {

    fun toDataset(priceData: List<PriceDataDTO>?): List<ProductChartData> {
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
        val currentTime = (System.currentTimeMillis() / 1000).toFloat()
        dataList.add(ProductChartData(currentTime, dataList.last().y, dataList.last().valid))
        return dataList.toList().sortedBy { it.x }.filter { it.x <= currentTime }
    }
}

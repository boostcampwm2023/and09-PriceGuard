package app.priceguard.data.graph

import app.priceguard.data.dto.PriceDataDTO

class GraphDataConverter {

    // TODO: 기간별로 데이터 필터링을 통해 해당 기간에 발생한 가격 변동만 추적하도록 구조 변경.
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

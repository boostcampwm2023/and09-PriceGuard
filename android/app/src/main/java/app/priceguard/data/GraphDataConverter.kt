package app.priceguard.data

import app.priceguard.data.dto.PriceDataDTO
import app.priceguard.data.graph.ProductChartData
import app.priceguard.materialchart.data.GraphMode

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
            dataList.add(
                ProductChartData(
                    x = dto.time / 1000,
                    y = dto.price.toFloat(),
                    valid = dto.isSoldOut.not()
                )
            )
        }

        return dataList.toList()
    }

    fun packWithEdgeData(
        list: List<ProductChartData>,
        period: GraphMode = GraphMode.DAY
    ): List<ProductChartData> {
        val currentTime = getCurrentTime()
        val startTime = getStartTime(period, currentTime)
        val sortedList = list.sortedBy { it.x }
        val filteredList = sortedList.filter { it.x in startTime..currentTime }
        val sievedList = sortedList.filter { it.x !in startTime..currentTime }
        val startData = if (sievedList.none()) {
            list.first()
        } else {
            sievedList.last()
        }

        return if (filteredList.isEmpty()) {
            listOf(
                ProductChartData(startTime, startData.y, startData.valid),
                ProductChartData(currentTime, list.last().y, list.last().valid)
            )
        } else {
            listOf(
                ProductChartData(
                    startTime,
                    startData.y,
                    startData.valid
                )
            ) + filteredList + ProductChartData(
                currentTime,
                filteredList.last().y,
                filteredList.last().valid
            )
        }
    }

    private fun getStartTime(period: GraphMode, currentTime: Float = getCurrentTime()): Float {
        return when (period) {
            GraphMode.DAY -> {
                currentTime - DAY
            }

            GraphMode.WEEK -> {
                currentTime - WEEK
            }

            GraphMode.MONTH -> {
                currentTime - MONTH
            }

            GraphMode.QUARTER -> {
                currentTime - QUARTER
            }
        }
    }

    private fun getCurrentTime(): Float {
        return (System.currentTimeMillis() / 1000).toFloat()
    }

    companion object {
        const val DAY = 86400
        const val WEEK = DAY * 7
        const val MONTH = DAY * 31
        const val QUARTER = MONTH * 3
    }
}

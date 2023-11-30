package app.priceguard.data.graph

import app.priceguard.materialchart.data.ChartData

data class ProductChartData(
    override val x: Float,
    override val y: Float,
    override val valid: Boolean
) : ChartData

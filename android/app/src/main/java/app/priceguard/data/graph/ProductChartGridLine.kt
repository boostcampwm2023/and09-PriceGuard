package app.priceguard.data.graph

import app.priceguard.materialchart.data.GridLine

data class ProductChartGridLine(
    override val name: String,
    override val value: Float
) : GridLine

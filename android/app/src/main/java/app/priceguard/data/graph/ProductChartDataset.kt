package app.priceguard.data.graph

import app.priceguard.materialchart.data.ChartData
import app.priceguard.materialchart.data.ChartDataset
import app.priceguard.materialchart.data.GraphMode
import app.priceguard.materialchart.data.GridLine

data class ProductChartDataset(
    override val showXAxis: Boolean,
    override val showYAxis: Boolean,
    override val isInteractive: Boolean,
    override val graphMode: GraphMode,
    override val data: List<ChartData>,
    override val gridLines: List<GridLine>
) : ChartDataset

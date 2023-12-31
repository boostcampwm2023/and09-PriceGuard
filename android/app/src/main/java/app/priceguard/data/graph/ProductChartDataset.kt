package app.priceguard.data.graph

import app.priceguard.materialchart.data.ChartDataset
import app.priceguard.materialchart.data.GraphMode
import app.priceguard.materialchart.data.GridLine

data class ProductChartDataset(
    override val showXAxis: Boolean,
    override val showYAxis: Boolean,
    override val isInteractive: Boolean,
    override val graphMode: GraphMode,
    override val xLabel: String,
    override val yLabel: String,
    override val data: List<ProductChartData>,
    override val gridLines: List<GridLine>
) : ChartDataset

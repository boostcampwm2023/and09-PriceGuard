package app.priceguard.ui.home

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.priceguard.R
import app.priceguard.data.graph.ProductChartData
import app.priceguard.data.graph.ProductChartDataset
import app.priceguard.databinding.ItemProductSummaryBinding
import app.priceguard.materialchart.data.GraphMode

class ProductSummaryAdapter(private val productSummaryClickListener: ProductSummaryClickListener) :
    ListAdapter<ProductSummary, ProductSummaryAdapter.ViewHolder>(diffUtil) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        return getItem(position).productCode.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemProductSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, productSummaryClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    class ViewHolder(
        private val binding: ItemProductSummaryBinding,
        private val productSummaryClickListener: ProductSummaryClickListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductSummary) {
            with(binding) {
                resetListener()
                summary = item
                setViewType(item)
                setClickListener(item.productCode)
                setGraph(item.priceData)
            }
        }

        private fun ItemProductSummaryBinding.resetListener() {
            msProduct.setOnCheckedChangeListener(null)
        }

        private fun ItemProductSummaryBinding.setViewType(item: ProductSummary) {
            when (item) {
                is ProductSummary.RecommendedProductSummary -> {
                    tvProductRecommendRank.visibility = View.VISIBLE
                    msProduct.visibility = View.GONE
                    tvProductDiscountPercent.visibility = View.GONE
                    setRecommendRank(item)
                }

                is ProductSummary.UserProductSummary -> {
                    tvProductRecommendRank.visibility = View.GONE
                    msProduct.visibility = View.VISIBLE
                    msProduct.isChecked = item.isAlarmOn
                    tvProductDiscountPercent.visibility = View.VISIBLE
                    setDiscount(item.discountPercent)
                    setSwitchListener(item)
                }
            }
        }

        private fun ItemProductSummaryBinding.setSwitchListener(item: ProductSummary) {
            if (msProduct.isChecked.not()) {
                msProduct.setThumbIconResource(R.drawable.ic_notifications_off)
            }
            msProduct.setOnCheckedChangeListener { _, isChecked ->
                productSummaryClickListener.onToggle(item.productCode, msProduct.isChecked)
                if (isChecked) {
                    msProduct.setThumbIconResource(R.drawable.ic_notifications_active)
                } else {
                    msProduct.setThumbIconResource(R.drawable.ic_notifications_off)
                }
            }
            msProduct.contentDescription = msProduct.context.getString(R.string.single_product_notification_toggle, item.title)
        }

        private fun ItemProductSummaryBinding.setDiscount(discount: Float) {
            tvProductDiscountPercent.text =
                if (discount > 0) {
                    tvProductDiscountPercent.context.getString(
                        R.string.add_plus,
                        tvProductDiscountPercent.context.getString(R.string.percent, discount)
                    )
                } else {
                    tvProductDiscountPercent.context.getString(
                        R.string.percent,
                        discount
                    )
                }
            val value = TypedValue()
            tvProductDiscountPercent.context.theme.resolveAttribute(
                if (discount > 0) android.R.attr.colorPrimary else android.R.attr.colorError,
                value,
                true
            )
            tvProductDiscountPercent.setTextColor(value.data)
            tvProductDiscountPercent.contentDescription = tvProductDiscountPercent.context.getString(R.string.target_price_delta, tvProductDiscountPercent.text)
        }

        private fun ItemProductSummaryBinding.setRecommendRank(item: ProductSummary.RecommendedProductSummary) {
            tvProductRecommendRank.text = tvProductRecommendRank.context.getString(
                R.string.recommand_rank, item.recommendRank
            )
            tvProductRecommendRank.contentDescription = tvProductRecommendRank.context.getString(R.string.current_rank_info, item.recommendRank)
        }

        private fun ItemProductSummaryBinding.setClickListener(code: String) {
            cvProduct.setOnClickListener {
                productSummaryClickListener.onClick(code)
            }
        }

        private fun ItemProductSummaryBinding.setGraph(data: List<ProductChartData>) {
            chGraph.dataset = ProductChartDataset(
                showXAxis = false,
                showYAxis = false,
                isInteractive = false,
                graphMode = GraphMode.WEEK,
                xLabel = chGraph.context.getString(R.string.date_text),
                yLabel = chGraph.context.getString(R.string.price_text),
                data = data,
                gridLines = listOf()
            )
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ProductSummary>() {
            override fun areContentsTheSame(oldItem: ProductSummary, newItem: ProductSummary) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ProductSummary, newItem: ProductSummary) =
                oldItem.productCode == newItem.productCode
        }
    }
}

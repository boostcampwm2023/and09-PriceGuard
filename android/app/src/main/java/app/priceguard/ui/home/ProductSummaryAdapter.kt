package app.priceguard.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.priceguard.R
import app.priceguard.databinding.ItemProductSummaryBinding

class ProductSummaryAdapter :
    ListAdapter<ProductSummary, ProductSummaryAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemProductSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    class ViewHolder(private val binding: ItemProductSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductSummary) {
            binding.setBinding(item)
        }

        private fun ItemProductSummaryBinding.setBinding(item: ProductSummary) {
            summary = item
            when (item) {
                is ProductSummary.RecommendedProductSummary -> {
                    tvProductRecommendRank.visibility = View.VISIBLE
                    msProduct.visibility = View.GONE
                    setRecommendRank(item)
                }

                is ProductSummary.UserProductSummary -> {
                    tvProductRecommendRank.visibility = View.GONE
                    msProduct.visibility = View.VISIBLE
                    setSwitchListener()
                }
            }
        }

        private fun ItemProductSummaryBinding.setSwitchListener() {
            msProduct.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    // TODO: 푸쉬 알람 설정 추가
                    msProduct.setThumbIconResource(R.drawable.ic_notifications_active)
                } else {
                    // TODO: 푸쉬 알람 설정 제거
                    msProduct.setThumbIconResource(R.drawable.ic_notifications_off)
                }
            }
        }

        private fun ItemProductSummaryBinding.setRecommendRank(item: ProductSummary.RecommendedProductSummary) {
            tvProductRecommendRank.text =
                tvProductRecommendRank.context.getString(
                    R.string.recommand_rank,
                    item.recommendRank
                )
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ProductSummary>() {
            override fun areContentsTheSame(oldItem: ProductSummary, newItem: ProductSummary) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ProductSummary, newItem: ProductSummary) =
                oldItem.readId() == newItem.readId()
        }
    }
}

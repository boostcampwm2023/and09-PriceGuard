package app.priceguard.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.priceguard.databinding.ItemProductSummaryBinding

class ProductSummaryAdapter : ListAdapter<ProductSummary, ProductSummaryAdapter.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = currentList[position]
        holder.bind(item)
    }

    class ViewHolder(private val binding: ItemProductSummaryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ProductSummary) {
            with(binding) {
                summary = item
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ProductSummary>() {
            override fun areContentsTheSame(oldItem: ProductSummary, newItem: ProductSummary) =
                oldItem == newItem

            override fun areItemsTheSame(oldItem: ProductSummary, newItem: ProductSummary) =
                oldItem.id == newItem.id
        }
    }
}

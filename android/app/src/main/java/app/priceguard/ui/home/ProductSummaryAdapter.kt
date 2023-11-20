package app.priceguard.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.priceguard.databinding.ItemProductSummaryBinding

class ProductSummaryAdapter(private val items: List<ProductSummary>) : RecyclerView.Adapter<ProductSummaryAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
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
}

package app.priceguard.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.priceguard.databinding.ItemMyPageListBinding

data class SettingItem(
    val icon: Int,
    val title: String
)

class MyPageSettingAdapter(private val items: List<SettingItem>) : RecyclerView.Adapter<ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMyPageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }
}

class ViewHolder(private val binding: ItemMyPageListBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: SettingItem) {
        with(binding) {
            ivMyPageItemIcon.setImageResource(item.icon)
            settingInfo = item
        }
    }
}

package app.priceguard.ui.home.mypage

import android.R
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.priceguard.databinding.ItemMyPageListBinding

class MyPageSettingAdapter(
    private val items: List<SettingItemInfo>,
    private val listener: MyPageSettingItemClickListener
) : RecyclerView.Adapter<MyPageSettingAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMyPageListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, listener)
    }

    class ViewHolder(private val binding: ItemMyPageListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SettingItemInfo, clickListener: MyPageSettingItemClickListener) {
            with(binding) {
                settingItemInfo = item
                listener = clickListener

                if (item.id == Setting.DELETE_ACCOUNT) {
                    val typedValue = TypedValue()
                    binding.root.context.theme.resolveAttribute(R.attr.colorError, typedValue, true)
                    tvMyPageItemTitle.setTextColor(typedValue.data)
                }
            }
        }
    }
}

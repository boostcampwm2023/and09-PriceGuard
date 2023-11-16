package app.priceguard.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.priceguard.R
import app.priceguard.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMyPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMyPageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSettingAdapter()
    }

    private fun initSettingAdapter() {
        val items = mutableListOf(
            SettingItem(R.drawable.ic_notification, getString(R.string.notification_setting)),
            SettingItem(R.drawable.ic_light_mode, getString(R.string.theme_setting)),
            SettingItem(R.drawable.ic_logout, getString(R.string.logout))
        )
        binding.rvMyPageSetting.adapter = MyPageSettingAdapter(items)
    }
}

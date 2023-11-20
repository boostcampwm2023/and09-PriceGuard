package app.priceguard.ui.home.mypage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.priceguard.R
import app.priceguard.databinding.FragmentMyPageBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyPageFragment : Fragment() {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSettingAdapter()
    }

    private fun initSettingAdapter() {
        val items = mutableListOf(
            SettingInfo(R.drawable.ic_notification, getString(R.string.notification_setting)),
            SettingInfo(R.drawable.ic_light_mode, getString(R.string.theme_setting)),
            SettingInfo(R.drawable.ic_logout, getString(R.string.logout))
        )
        binding.rvMyPageSetting.adapter = MyPageSettingAdapter(items)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

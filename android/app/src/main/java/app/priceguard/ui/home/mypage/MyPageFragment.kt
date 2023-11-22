package app.priceguard.ui.home.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import app.priceguard.R
import app.priceguard.data.repository.TokenRepository
import app.priceguard.databinding.FragmentMyPageBinding
import app.priceguard.ui.home.mypage.MyPageViewModel.MyPageEvent
import app.priceguard.ui.intro.IntroActivity
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : Fragment() {

    @Inject
    lateinit var tokenRepository: TokenRepository
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(layoutInflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSettingAdapter()

        repeatOnStarted {
            viewModel.event.collect { event ->
                when (event) {
                    is MyPageEvent.StartIntroAndExitHome -> startIntroAndExitHome()
                }
            }
        }
    }

    private fun initSettingAdapter() {
        val settingItems = initSettingItem()
        binding.rvMyPageSetting.adapter =
            MyPageSettingAdapter(
                settingItems,
                object : MyPageSettingItemClickListener {
                    override fun onClick(setting: Setting) {
                        when (setting) {
                            Setting.NOTIFICATION -> {
                                // TODO: 알람 설정
                            }

                            Setting.THEME -> {
                                // TODO: 테마 설정
                            }

                            Setting.LICENSE -> {
                                // TODO: 오픈소스 라이선스
                            }

                            Setting.LOGOUT -> {
                                showLogoutConfirmDialog()
                            }
                        }
                    }
                }
            )
    }

    private fun initSettingItem(): List<SettingItemInfo> {
        return listOf(
            SettingItemInfo(
                Setting.NOTIFICATION,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_notification),
                getString(R.string.notification_setting)
            ),
            SettingItemInfo(
                Setting.THEME,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_light_mode),
                getString(R.string.theme_setting)
            ),
            SettingItemInfo(
                Setting.LICENSE,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_android),
                getString(R.string.opensource_license)
            ),
            SettingItemInfo(
                Setting.LOGOUT,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_logout),
                getString(R.string.logout)
            )
        )
    }

    private fun showLogoutConfirmDialog() {
        MaterialAlertDialogBuilder(requireActivity())
            .setTitle(getString(R.string.logout_confirm_title))
            .setMessage(getString(R.string.logout_confirm_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ -> viewModel.logout() }
            .setNegativeButton(R.string.no) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun startIntroAndExitHome() {
        val intent = Intent(requireActivity(), IntroActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

package app.priceguard.ui.home.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.databinding.FragmentMyPageBinding
import app.priceguard.ui.data.DialogConfirmAction
import app.priceguard.ui.home.mypage.MyPageViewModel.MyPageEvent
import app.priceguard.ui.intro.IntroActivity
import app.priceguard.ui.util.ConfirmDialogFragment
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.openNotificationSettings
import app.priceguard.ui.util.safeNavigate
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyPageFragment : Fragment(), ConfirmDialogFragment.OnDialogResultListener {

    @Inject
    lateinit var tokenRepository: TokenRepository
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private val myPageViewModel: MyPageViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(layoutInflater, container, false)
        binding.viewModel = myPageViewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSettingAdapter()

        repeatOnStarted {
            myPageViewModel.event.collect { event ->
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
                                requireContext().openNotificationSettings()
                            }

                            Setting.THEME -> {
                                findNavController().safeNavigate(R.id.action_myPageFragment_to_themeDialogFragment)
                            }

                            Setting.LICENSE -> {
                                startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
                            }

                            Setting.LOGOUT -> {
                                showConfirmationDialogForResult(
                                    R.string.logout_confirm_title,
                                    R.string.logout_confirm_message,
                                    Setting.LOGOUT.ordinal
                                )
                            }

                            Setting.DELETE_ACCOUNT -> {
                                val intent = Intent(requireActivity(), DeleteAccountActivity::class.java)
                                intent.putExtra("email", myPageViewModel.state.value.email)
                                startActivity(intent)
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
            ),
            SettingItemInfo(
                Setting.DELETE_ACCOUNT,
                ContextCompat.getDrawable(requireActivity(), R.drawable.ic_close_red),
                getString(R.string.delete_account)
            )
        )
    }

    private fun showConfirmationDialogForResult(
        @StringRes title: Int,
        @StringRes message: Int,
        requestCode: Int
    ) {
        val tag = "confirm_dialog_fragment_from_activity"
        if (requireActivity().supportFragmentManager.findFragmentByTag(tag) != null) return

        val dialogFragment = ConfirmDialogFragment()
        val bundle = Bundle()
        bundle.putString("title", getString(title))
        bundle.putString("message", getString(message))
        bundle.putString("actionString", DialogConfirmAction.CUSTOM.name)
        bundle.putInt("requestCode", requestCode)
        dialogFragment.arguments = bundle
        dialogFragment.setOnDialogResultListener(this)
        dialogFragment.show(requireActivity().supportFragmentManager, tag)
    }

    private fun startIntroAndExitHome() {
        val intent = Intent(requireActivity(), IntroActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDialogResult(requestCode: Int, result: Boolean) {
        when (requestCode) {
            Setting.LOGOUT.ordinal -> myPageViewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

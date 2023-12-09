package app.priceguard.ui.home.mypage

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import app.priceguard.R
import app.priceguard.data.repository.token.TokenRepository
import app.priceguard.databinding.FragmentMyPageBinding
import app.priceguard.ui.ConfirmDialogFragment
import app.priceguard.ui.data.DialogConfirmAction
import app.priceguard.ui.home.mypage.MyPageViewModel.MyPageEvent
import app.priceguard.ui.intro.IntroActivity
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.ui.openNotificationSettings
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
                                findNavController().navigate(R.id.action_myPageFragment_to_themeDialogFragment)
                            }

                            Setting.LICENSE -> {
                                startActivity(Intent(activity, OssLicensesMenuActivity::class.java))
                            }

                            Setting.LOGOUT -> {
                                showConfirmationDialogForResult()
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

    private fun showConfirmationDialogForResult() {
        val dialogFragment = ConfirmDialogFragment()
        val bundle = Bundle()
        bundle.putString("title", getString(R.string.logout_confirm_title))
        bundle.putString("message", getString(R.string.logout_confirm_message))
        bundle.putString("actionString", DialogConfirmAction.CUSTOM.name)
        dialogFragment.arguments = bundle
        dialogFragment.setOnDialogResultListener(this)
        dialogFragment.show(requireActivity().supportFragmentManager, "confirm_dialog_fragment_from_fragment")
    }

    private fun startIntroAndExitHome() {
        val intent = Intent(requireActivity(), IntroActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onDialogResult(result: Boolean) {
        if (result) {
            myPageViewModel.logout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

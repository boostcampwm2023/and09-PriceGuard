package app.priceguard.ui.home.theme

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import app.priceguard.R
import app.priceguard.data.datastore.ConfigDataSource
import app.priceguard.databinding.FragmentThemeDialogBinding
import app.priceguard.ui.PriceGuardApp
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ThemeDialogFragment : DialogFragment() {

    @Inject
    lateinit var configDataSource: ConfigDataSource
    private var _binding: FragmentThemeDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentThemeDialogBinding.inflate(requireActivity().layoutInflater)
        val view = binding.root

        setCheckedButton()
        checkDynamicThemeSupport()

        return MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.ThemeOverlay_App_MaterialAlertDialog
        ).apply {
            setView(view)
            setPositiveButton(R.string.confirm) { _, _ ->
                val dynamicMode = applyDynamicMode()
                val darkMode = applyDarkMode()
                requireActivity().recreate()

                saveTheme(dynamicMode, darkMode)
                dismiss()
            }
        }.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun applyDynamicMode() = when (binding.rgDynamicColor.checkedRadioButtonId) {
        R.id.rb_yes -> {
            DynamicColors.applyToActivitiesIfAvailable(requireActivity().application)
            PriceGuardApp.MODE_DYNAMIC
        }

        else -> {
            DynamicColors.applyToActivitiesIfAvailable(
                requireActivity().application,
                DynamicColorsOptions.Builder()
                    .setThemeOverlay(R.style.Theme_PriceGuard).build()
            )
            PriceGuardApp.MODE_DYNAMIC_NO
        }
    }

    private fun applyDarkMode() = when (binding.rgDarkMode.checkedRadioButtonId) {
        R.id.rb_system -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            PriceGuardApp.MODE_SYSTEM
        }

        R.id.rb_light -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            PriceGuardApp.MODE_LIGHT
        }

        R.id.rb_dark -> {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            PriceGuardApp.MODE_DARK
        }

        else -> {
            PriceGuardApp.MODE_SYSTEM
        }
    }

    private fun checkDynamicThemeSupport() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            // Disable Dynamic Theme Radio Group
            (0 until binding.rgDynamicColor.childCount).forEach { idx ->
                binding.rgDynamicColor.getChildAt(idx).isEnabled = false
            }
        }
    }

    private fun saveTheme(dynamicMode: Int, darkMode: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            configDataSource.saveDynamicMode(dynamicMode)
            configDataSource.saveDarkMode(darkMode)
        }
    }

    private fun setCheckedButton() {
        lifecycleScope.launch {
            val dynamicColorMode = configDataSource.getDynamicMode()
            val darkMode = configDataSource.getDarkMode()

            binding.rgDynamicColor.check(
                when (dynamicColorMode) {
                    PriceGuardApp.MODE_DYNAMIC -> {
                        R.id.rb_yes
                    }

                    else -> {
                        R.id.rb_no
                    }
                }
            )

            binding.rgDarkMode.check(
                when (darkMode) {
                    PriceGuardApp.MODE_LIGHT -> {
                        R.id.rb_light
                    }

                    PriceGuardApp.MODE_DARK -> {
                        R.id.rb_dark
                    }

                    else -> {
                        R.id.rb_system
                    }
                }
            )
        }
    }
}

package app.priceguard.ui.home.theme

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import app.priceguard.R
import app.priceguard.data.datastore.ConfigDataSource
import app.priceguard.databinding.FragmentThemeDialogBinding
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ThemeDialogFragment : DialogFragment() {

    @Inject
    lateinit var configDataSource: ConfigDataSource

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding: FragmentThemeDialogBinding =
            FragmentThemeDialogBinding.inflate(requireActivity().layoutInflater)
        val view = binding.root

        setCheckedButton(binding)

        return MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.ThemeOverlay_App_MaterialAlertDialog
        ).apply {
            setView(view)
            setPositiveButton(R.string.confirm) { _, _ ->
                val dynamicMode = when (binding.rgDynamicColor.checkedRadioButtonId) {
                    R.id.rb_yes -> {
                        DynamicColors.applyToActivitiesIfAvailable(requireActivity().application)
                        requireActivity().recreate()
                        MODE_DYNAMIC
                    }

                    else -> {
                        DynamicColors.applyToActivitiesIfAvailable(
                            requireActivity().application,
                            DynamicColorsOptions.Builder()
                                .setThemeOverlay(R.style.Theme_PriceGuard).build()
                        )
                        requireActivity().recreate()
                        MODE_DYNAMIC_NO
                    }
                }

                val darkMode = when (binding.rgDarkMode.checkedRadioButtonId) {
                    R.id.rb_system -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        MODE_SYSTEM
                    }

                    R.id.rb_light -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        MODE_LIGHT
                    }

                    R.id.rb_dark -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        MODE_DARK
                    }

                    else -> {
                        MODE_SYSTEM
                    }
                }
                saveTheme(dynamicMode, darkMode)
                dismiss()
            }
        }.create()
    }

    private fun saveTheme(dynamicMode: Int, darkMode: Int) {
        this.lifecycleScope.launch(Dispatchers.IO) {
            configDataSource.saveDynamicMode(dynamicMode)
            configDataSource.saveDarkMode(darkMode)
        }
    }

    private fun setCheckedButton(binding: FragmentThemeDialogBinding) {
        lifecycleScope.launch {
            val dynamicColorMode = configDataSource.getDynamicMode()
            val darkMode = configDataSource.getDarkMode()

            withContext(Dispatchers.Main) {
                binding.rgDynamicColor.check(
                    when (dynamicColorMode) {
                        MODE_DYNAMIC -> {
                            R.id.rb_yes
                        }

                        else -> {
                            R.id.rb_no
                        }
                    }
                )

                binding.rgDarkMode.check(
                    when (darkMode) {
                        MODE_LIGHT -> {
                            R.id.rb_light
                        }

                        MODE_DARK -> {
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

    companion object {
        const val MODE_SYSTEM = 0
        const val MODE_LIGHT = 1
        const val MODE_DARK = 2

        const val MODE_DYNAMIC_NO = 0
        const val MODE_DYNAMIC = 1
    }
}

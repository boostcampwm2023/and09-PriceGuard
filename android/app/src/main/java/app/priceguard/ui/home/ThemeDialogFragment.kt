package app.priceguard.ui.home

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import app.priceguard.R
import app.priceguard.databinding.FragmentThemeDialogBinding
import com.google.android.material.color.DynamicColors
import com.google.android.material.color.DynamicColorsOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ThemeDialogFragment : DialogFragment() {

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
                                .setThemeOverlay(R.style.Base_Theme_PriceGuard).build()
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
                saveModeWithPreference(dynamicMode, darkMode)

                dismiss()
            }
        }.create()
    }

    private fun saveModeWithPreference(dynamicMode: Int, darkMode: Int) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putInt("DynamicColor", dynamicMode)
        editor.putInt("DarkMode", darkMode)
        editor.apply()
    }

    private fun setCheckedButton(binding: FragmentThemeDialogBinding) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val dynamicColorMode = sharedPreferences.getInt("DynamicColor", MODE_DYNAMIC_NO)
        val darkMode = sharedPreferences.getInt("DarkMode", MODE_SYSTEM)

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

    companion object {
        const val MODE_SYSTEM = 0
        const val MODE_LIGHT = 1
        const val MODE_DARK = 2

        const val MODE_DYNAMIC_NO = 0
        const val MODE_DYNAMIC = 1
    }
}

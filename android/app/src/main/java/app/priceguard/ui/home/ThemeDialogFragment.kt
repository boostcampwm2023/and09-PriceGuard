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
                        "dynamicColor"
                    }

                    else -> {
                        DynamicColors.applyToActivitiesIfAvailable(
                            requireActivity().application,
                            DynamicColorsOptions.Builder()
                                .setThemeOverlay(R.style.Theme_PriceGuard).build()
                        )
                        requireActivity().recreate()
                        "default"
                    }
                }

                val darkMode = when (binding.rgDarkMode.checkedRadioButtonId) {
                    R.id.rb_system -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        "system"
                    }

                    R.id.rb_light -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        "light"
                    }

                    R.id.rb_dark -> {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        "dark"
                    }

                    else -> {
                        "system"
                    }
                }
                saveModeWithPreference(dynamicMode, darkMode)

                dismiss()
            }
        }.create()
    }

    private fun saveModeWithPreference(dynamicMode: String, darkMode: String) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("DynamicColor", dynamicMode)
        editor.putString("DarkMode", darkMode)
        editor.apply()
    }

    private fun setCheckedButton(binding: FragmentThemeDialogBinding) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val dynamicColorMode = sharedPreferences.getString("DynamicColor", "default")
        val darkMode = sharedPreferences.getString("DarkMode", "system")

        binding.rgDynamicColor.check(
            when (dynamicColorMode) {
                "dynamicColor" -> {
                    R.id.rb_yes
                }

                else -> {
                    R.id.rb_no
                }
            }
        )

        binding.rgDarkMode.check(
            when (darkMode) {
                "light" -> {
                    R.id.rb_light
                }

                "dark" -> {
                    R.id.rb_dark
                }

                else -> {
                    R.id.rb_system
                }
            }
        )
    }
}

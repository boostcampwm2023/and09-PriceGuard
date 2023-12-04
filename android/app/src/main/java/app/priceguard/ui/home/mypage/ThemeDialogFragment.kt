package app.priceguard.ui.home.mypage

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.DialogFragment
import app.priceguard.R
import app.priceguard.databinding.FragmentThemeDialogBinding
import com.google.android.material.color.DynamicColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class ThemeDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding: FragmentThemeDialogBinding =
            FragmentThemeDialogBinding.inflate(requireActivity().layoutInflater)
        val view = binding.root

//        setCheckedButton(binding)
        initListener(binding)

        return MaterialAlertDialogBuilder(
            requireActivity(),
            R.style.ThemeOverlay_App_MaterialAlertDialog
        ).apply {
            setView(view)
            setPositiveButton(R.string.confirm) { _, _ -> dismiss() }
        }.create()
    }

    private fun initListener(binding: FragmentThemeDialogBinding) {
        binding.rgDynamicColor.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.rb_yes -> {
                    DynamicColors.applyToActivitiesIfAvailable(requireActivity().application)
                    "dynamicColor"
                }

                R.id.rb_no -> {
                    DynamicColors.applyToActivitiesIfAvailable(requireActivity().application)
                    "default"
                }

                else -> {
                    "default"
                }
            }
            saveModeWithPreference(mode)
        }

        binding.rgDarkMode.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
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
            saveModeWithPreference(mode)
        }
    }

    private fun saveModeWithPreference(mode: String) {
        val sharedPreferences =
            requireActivity().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val editor = sharedPreferences.edit()
        editor.putString("DynamicColor", mode)
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

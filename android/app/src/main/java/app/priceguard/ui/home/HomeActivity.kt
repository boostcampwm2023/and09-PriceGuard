package app.priceguard.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import app.priceguard.R
import app.priceguard.databinding.ActivityHomeBinding
import app.priceguard.ui.util.ui.openNotificationSettings
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var snackbar: Snackbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initSnackBar()
        checkForGooglePlayServices()
        setBottomNavigationBar()
        askNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        checkForGooglePlayServices()

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            dismissSnackbar()
        } else {
            showNotificationOffSnackbar()
        }
    }

    private fun checkForGooglePlayServices() {
        val availability = GoogleApiAvailability().isGooglePlayServicesAvailable(this)

        if (availability != ConnectionResult.SUCCESS) {
            GoogleApiAvailability().makeGooglePlayServicesAvailable(this)
        }
    }

    private fun initSnackBar() {
        snackbar = Snackbar.make(
            binding.root,
            getString(R.string.currently_notification_disabled),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(R.string.setting)) {
            openNotificationSettings()
        }.setAnchorView(binding.bottomNavigation)
    }

    private fun setBottomNavigationBar() {
        val navController = binding.navHostHome.getFragment<NavHostFragment>().navController
        binding.bottomNavigation.setupWithNavController(navController)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("NOTIFICATION", "PERMISSION GRANTED")
        } else {
            showNotificationOffSnackbar()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Allowed
                    Log.d("NOTIFICATION", "PERMISSION GRANTED")
                }

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    // Explicitly denied
                    showNotificationOffSnackbar()
                }

                else -> {
                    // Initial cases
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun showNotificationOffSnackbar() {
        if (snackbar.isShown) return
        snackbar.show()
    }

    private fun dismissSnackbar() {
        if (snackbar.isShown) {
            snackbar.dismiss()
        }
    }
}

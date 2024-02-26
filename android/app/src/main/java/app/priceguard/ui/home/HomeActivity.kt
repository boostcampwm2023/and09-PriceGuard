package app.priceguard.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import app.priceguard.R
import app.priceguard.databinding.ActivityHomeBinding
import app.priceguard.service.UpdateTokenWorker
import app.priceguard.ui.util.SystemNavigationColorState
import app.priceguard.ui.util.applySystemNavigationBarColor
import app.priceguard.ui.util.openNotificationSettings
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var snackbar: Snackbar
    private val appUpdateManager = AppUpdateManagerFactory.create(this)
    private val updateType = AppUpdateType.IMMEDIATE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.applySystemNavigationBarColor(SystemNavigationColorState.BOTTOM_NAVIGATION)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enqueueWorker()
        initSnackBar()
        checkForGooglePlayServices()
        setBottomNavigationBar()
        askNotificationPermission()
    }

    override fun onResume() {
        super.onResume()
        checkForGooglePlayServices()
        checkAppUpdates()

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

    private fun enqueueWorker() {
        val saveRequest =
            PeriodicWorkRequestBuilder<UpdateTokenWorker>(730, TimeUnit.HOURS)
                .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "saveRequest",
            ExistingPeriodicWorkPolicy.UPDATE,
            saveRequest
        )
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

    private fun checkAppUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
            when (result.resultCode) {
                RESULT_CANCELED -> {
                    Toast.makeText(this, getString(R.string.update_cancel_warning), Toast.LENGTH_LONG).show()
                }
                com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                    Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_LONG).show()
                }
            }
        }

        appUpdateInfoTask.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                info.isUpdateTypeAllowed(updateType)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build()
                )
            }

            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    activityResultLauncher,
                    AppUpdateOptions.newBuilder(updateType).build()
                )
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

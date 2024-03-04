package app.priceguard.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
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
import app.priceguard.ui.util.showConfirmDialog
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var snackbar: Snackbar
    private lateinit var appUpdateManager: AppUpdateManager
    private lateinit var flexibleAppUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var immediateAppUpdateResultLauncher: ActivityResultLauncher<IntentSenderRequest>
    private val updateListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            appUpdateManager.completeUpdate()
        }

        if (state.installStatus() == InstallStatus.FAILED) {
            Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.applySystemNavigationBarColor(SystemNavigationColorState.BOTTOM_NAVIGATION)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAppUpdate()
        enqueueWorker()
        initSnackBar()
        checkForGooglePlayServices()
        setBottomNavigationBar()
        askNotificationPermission()
        checkAppUpdates()
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

    override fun onDestroy() {
        super.onDestroy()
        appUpdateManager.unregisterListener(updateListener)
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

    private fun setupAppUpdate() {
        appUpdateManager = AppUpdateManagerFactory.create(this)
        flexibleAppUpdateResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
                when (result.resultCode) {
                    RESULT_CANCELED -> {
                        showConfirmDialog(getString(R.string.warning), getString(R.string.update_cancel_warning))
                    }

                    com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                        Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        immediateAppUpdateResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result: ActivityResult ->
                when (result.resultCode) {
                    RESULT_CANCELED -> {
                        showConfirmDialog(getString(R.string.warning), getString(R.string.update_cancel_warning))
                    }

                    com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                        Toast.makeText(this, getString(R.string.update_failed), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        appUpdateManager.registerListener(updateListener)
    }

    private fun checkAppUpdates() {
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    flexibleAppUpdateResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                )
            }

            if (info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE) &&
                info.updatePriority() >= 3
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    info,
                    immediateAppUpdateResultLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }

            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                if (info.updatePriority() >= 3) {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        immediateAppUpdateResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                    )
                } else {
                    appUpdateManager.startUpdateFlowForResult(
                        info,
                        flexibleAppUpdateResultLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build()
                    )
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

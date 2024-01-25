package app.priceguard.ui.home.mypage

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.R
import app.priceguard.databinding.ActivityDeleteAccountBinding
import app.priceguard.ui.data.DialogConfirmAction
import app.priceguard.ui.login.LoginActivity
import app.priceguard.ui.util.SystemNavigationColorState
import app.priceguard.ui.util.applySystemNavigationBarColor
import app.priceguard.ui.util.lifecycle.repeatOnStarted
import app.priceguard.ui.util.onThrottleClick
import app.priceguard.ui.util.showConfirmDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeleteAccountBinding
    private val deleteAccountViewModel: DeleteAccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.applySystemNavigationBarColor(SystemNavigationColorState.SURFACE)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = deleteAccountViewModel
        binding.lifecycleOwner = this

        deleteAccountViewModel.updateEmail(intent.getStringExtra("email") ?: "")

        initView()
        initCollector()
    }

    private fun initView() {
        binding.mtDeleteAccountTopbar.setNavigationOnClickListener {
            finish()
        }

        binding.btnDeleteAccount.onThrottleClick {
            deleteAccountViewModel.deleteAccount()
        }
    }

    private fun initCollector() {
        repeatOnStarted {
            deleteAccountViewModel.state.collect { state ->
                binding.btnDeleteAccount.isEnabled = state.isDeleteEnabled
            }
        }

        repeatOnStarted {
            deleteAccountViewModel.event.collect { event ->
                when (event) {
                    DeleteAccountViewModel.DeleteAccountEvent.Logout -> {
                        Toast.makeText(
                            this@DeleteAccountActivity,
                            getString(R.string.success_delete_account),
                            Toast.LENGTH_SHORT
                        ).show()
                        goBackToLoginActivity()
                    }
                    DeleteAccountViewModel.DeleteAccountEvent.WrongPassword -> {
                        showConfirmDialog(
                            getString(R.string.error_password),
                            getString(R.string.wrong_password),
                            DialogConfirmAction.NOTHING
                        )
                    }

                    DeleteAccountViewModel.DeleteAccountEvent.UndefinedError -> {
                        showConfirmDialog(
                            getString(R.string.error),
                            getString(R.string.undefined_error),
                            DialogConfirmAction.NOTHING
                        )
                    }
                }
            }
        }
    }

    private fun goBackToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

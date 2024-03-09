package app.priceguard.ui.login.findpassword

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import app.priceguard.R
import app.priceguard.databinding.ActivityFindPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFindPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isFindPassword = intent.getBooleanExtra("isFindPassword", true)

        val bundle = Bundle()
        bundle.putBoolean("isFindPassword", isFindPassword)

        val navController = binding.fcvFindPassword.getFragment<NavHostFragment>().navController
        navController.setGraph(R.navigation.nav_graph_find_password, bundle)
    }
}

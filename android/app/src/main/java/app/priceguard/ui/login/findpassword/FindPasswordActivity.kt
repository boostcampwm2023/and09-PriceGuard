package app.priceguard.ui.login.findpassword

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.databinding.ActivityFindPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FindPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFindPasswordBinding.inflate(layoutInflater)

        setContentView(binding.root)
    }
}

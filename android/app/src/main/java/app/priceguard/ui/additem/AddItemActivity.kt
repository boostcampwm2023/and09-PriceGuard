package app.priceguard.ui.additem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.databinding.ActivityAddItemBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

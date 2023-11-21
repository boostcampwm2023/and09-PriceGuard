package app.priceguard.ui.main.additem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.priceguard.databinding.ActivityAddItemBinding

class AddItemActivity : AppCompatActivity() {

    lateinit var binding: ActivityAddItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}

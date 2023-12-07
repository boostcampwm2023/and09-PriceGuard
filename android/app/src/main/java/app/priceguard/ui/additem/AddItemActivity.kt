package app.priceguard.ui.additem

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import app.priceguard.R
import app.priceguard.databinding.ActivityAddItemBinding
import app.priceguard.ui.additem.link.RegisterItemLinkFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddItemActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddItemBinding.inflate(layoutInflater)
        setStartDestination()
        setContentView(binding.root)
    }

    private fun setStartDestination() {
        val navController = binding.fcvAddItem.getFragment<NavHostFragment>().navController

        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { data ->
                val bundle = Bundle()
                bundle.putString("link", data)
                navController.navigate(R.id.registerItemLinkFragment, bundle)
            }
        } else if (intent.hasExtra("productCode") &&
            intent.hasExtra("productTitle") &&
            intent.hasExtra("productPrice") &&
            intent.hasExtra("isAdding")
        ) {
            val action =
                RegisterItemLinkFragmentDirections.actionRegisterItemLinkFragmentToSetTargetPriceFragment(
                    intent.getStringExtra("productCode") ?: "",
                    intent.getStringExtra("productTitle") ?: "",
                    intent.getIntExtra("productPrice", 0),
                    intent.getBooleanExtra("isAdding", true)
                )
            navController.navigate(action)
        }
    }
}

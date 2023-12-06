package app.priceguard.ui.additem

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
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
        if (intent.hasExtra("productCode") &&
            intent.hasExtra("productTitle") &&
            intent.hasExtra("productPrice") &&
            intent.hasExtra("isAdding")
        ) {
            val navController = binding.fcvAddItem.getFragment<NavHostFragment>().navController
            val action =
                RegisterItemLinkFragmentDirections.actionRegisterItemLinkFragmentToSetTargetPriceFragment(
                    intent.getStringExtra("productCode") ?: "",
                    intent.getStringExtra("productTitle") ?: "",
                    intent.getIntExtra("productPrice", 0),
                    intent.getBooleanExtra("isAdding", true)
                )
            navController.safeNavigate(action)
        }
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.getAction(direction.actionId)?.run { navigate(direction) }
    }
}

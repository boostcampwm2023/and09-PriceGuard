package app.priceguard.ui.slider

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import app.priceguard.R
import app.priceguard.databinding.ActivityRoundSliderBinding

class RoundSliderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRoundSliderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRoundSliderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.roundSlider.setMaxPercentValue(180)
        binding.roundSlider.setValue(10)
        binding.roundSlider.setSliderStrokeWidth(4)
        binding.roundSlider.setPointRange(100, 120)

        binding.roundSlider.setSliderValueChangeListener { value ->
            Log.d("slideValueChange", value.toString())
        }
    }
}

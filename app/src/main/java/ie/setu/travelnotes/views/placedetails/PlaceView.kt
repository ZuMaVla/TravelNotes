package ie.setu.travelnotes.views.placedetails

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ie.setu.travelnotes.databinding.DetailsPlaceBinding

class PlaceView : AppCompatActivity() {

    private lateinit var binding: DetailsPlaceBinding
    private lateinit var presenter: PlacePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailsPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)
        presenter = PlacePresenter(this)
    }
}
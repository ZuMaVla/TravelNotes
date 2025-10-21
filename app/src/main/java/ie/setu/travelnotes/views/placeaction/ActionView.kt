package ie.setu.travelnotes.views.placeaction

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import ie.setu.travelnotes.databinding.ActionPlaceBinding
import timber.log.Timber.i
import java.time.LocalDate

class ActionView : AppCompatActivity() {

    lateinit var binding: ActionPlaceBinding
    private lateinit var presenter: ActionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActionPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)
        presenter = ActionPresenter(this)
        val today = LocalDate.now()
        binding.travelPlaceDate.text = today.toString()
        binding.travelPlaceTitle.setText("My Place")
        binding.travelPlaceDescription.setText("Nice place")
        binding.chooseImage.setOnClickListener {
            presenter.doSelectImage()
        }
        binding.btnAddOrSave.setOnClickListener {
            presenter.doAddOrSave()
        }
        binding.travelPlaceDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val selectedDate = LocalDate.of(year, month + 1, day)
                    binding.travelPlaceDate.text = selectedDate.toString()
                },
                today.year,
                today.monthValue - 1,
                today.dayOfMonth
            )
            datePicker.show()
        }

    }

    fun updateImage(image: Uri){
        i("Image updated")
        Picasso.get()
            .load(image)
            .into(binding.travelPlaceImage)
//        binding.chooseImage.setText(R.string.change_placemark_image)
    }
}
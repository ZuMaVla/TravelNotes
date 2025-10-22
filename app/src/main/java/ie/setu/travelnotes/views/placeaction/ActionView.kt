package ie.setu.travelnotes.views.placeaction

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import ie.setu.travelnotes.R
import ie.setu.travelnotes.databinding.ActionPlaceBinding
import ie.setu.travelnotes.models.place.PlaceModel
import timber.log.Timber.i
import java.time.LocalDate

class ActionView : AppCompatActivity() {

    lateinit var binding: ActionPlaceBinding
    private lateinit var presenter: ActionPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActionPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        presenter = ActionPresenter(this)

        setSupportActionBar(binding.toolbarAdd)

        binding.chooseImage.setOnClickListener {
            presenter.doSelectImage()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Handle menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_save -> presenter.doAddOrSave()
            R.id.item_cancel -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateImage(image: Uri){
        i("Image updated")
        Picasso.get()
            .load(image)
            .into(binding.travelPlaceImage)
//        binding.chooseImage.setText(R.string.change_placemark_image)
    }

    fun showPlace(place: PlaceModel) {
        i(place.title)
        binding.travelPlaceTitle.setText(place.title)
        binding.travelPlaceDescription.setText(place.description)
        binding.travelPlaceDate.text = place.date.toString()
        Picasso.get()
            .load(place.image)
            .into(binding.travelPlaceImage)
        if (place.image != Uri.EMPTY) {
            binding.chooseImage.setText(R.string.change_place_image)
        }
        binding.travelPlaceDate.setOnClickListener {
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, day ->
                    val selectedDate = LocalDate.of(year, month + 1, day)
                    binding.travelPlaceDate.text = selectedDate.toString()
                },
                place.date.year,
                place.date.monthValue - 1,
                place.date.dayOfMonth
            )
            datePicker.show()
        }

    }

}
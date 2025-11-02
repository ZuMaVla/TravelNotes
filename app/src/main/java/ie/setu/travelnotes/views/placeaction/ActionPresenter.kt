package ie.setu.travelnotes.views.placeaction

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import ie.setu.travelnotes.main.MainApp
import ie.setu.travelnotes.models.place.PlaceModel
import ie.setu.travelnotes.views.map.MapView
import timber.log.Timber.i
import java.time.LocalDate

class ActionPresenter(private val view: ActionView) {

    var travelPlace = PlaceModel()
    var app: MainApp = view.application as MainApp
    private lateinit var imageIntentLauncher : ActivityResultLauncher<PickVisualMediaRequest>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    var edit = false

    init {
        if (view.intent.hasExtra("place_edit")) {
            edit = true
            travelPlace = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                view.intent.getParcelableExtra("place_edit", PlaceModel::class.java)!!
            } else {
                @Suppress("DEPRECATION")
                view.intent.getParcelableExtra<PlaceModel>("place_edit")!!
            }
        }
        else {
            val today = LocalDate.now()
            travelPlace.date = today
            travelPlace.title = ""
            travelPlace.description =""
            travelPlace.image = Uri.EMPTY
        }
        view.showPlace(travelPlace)
        registerImagePickerCallback()
        registerMapCallback()
    }

    fun doSelectImage() {
        i("Select image")
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()
        imageIntentLauncher.launch(request)
    }
    fun doSelectLocation() {
        i("Set Location Pressed")
        val launcherIntent = Intent(view, MapView::class.java)
        launcherIntent.putExtra("lat", travelPlace.lat)
        launcherIntent.putExtra("lng", travelPlace.lng)
        launcherIntent.putExtra("zoom", 15f) // Set a default zoom level
        mapIntentLauncher.launch(launcherIntent)
    }

    fun doAddOrSave() {
        val title = view.binding.travelPlaceTitle.text.toString()
        val description = view.binding.travelPlaceDescription.text.toString()

        if (title.length < 3 || title.length > 30) {
            view.showToast("Title must be between 3 and 30 characters")
            return
        }
        if (description.isEmpty()) {
            view.showToast("Description cannot be empty")
            return
        }

        val currentUser = app.currentUser
        if (currentUser != null) {
            travelPlace.title = title
            travelPlace.description = description
            travelPlace.date = LocalDate.parse(view.binding.travelPlaceDate.text.toString())

            val resultIntent = Intent()
            if (edit) {
                app.travelPlaces.updatePlace(currentUser.id, travelPlace.copy())
                resultIntent.putExtra("operation", "edit")
                resultIntent.putExtra("place_edited", travelPlace.copy()) 
            } else {
                travelPlace.userId = currentUser.id
                app.travelPlaces.createPlace(travelPlace.copy())
                resultIntent.putExtra("operation", "add")
            }
            i("add or save pressed")
            view.setResult(Activity.RESULT_OK, resultIntent)
            view.finish()
        }
    }

    private fun registerImagePickerCallback() {
        imageIntentLauncher = view.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            try{
                view.contentResolver
                    .takePersistableUriPermission(it!!,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION )
                travelPlace.image = it // The returned Uri
                i("IMG :: ${travelPlace.image}")
                view.updateImage(travelPlace.image)
            }
            catch(e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher = view.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            result ->
            when (result.resultCode) {
                AppCompatActivity.RESULT_OK -> {
                    if (result.data != null) {
                        i("Got Location ${result.data.toString()}")
                        val lat = result.data!!.getDoubleExtra("lat", 0.0)
                        val lng = result.data!!.getDoubleExtra("lng", 0.0)
                        i("Location == $lat, $lng")
                        travelPlace.lat = lat
                        travelPlace.lng = lng
                    } // end of if
                }
                AppCompatActivity.RESULT_CANCELED -> { } else -> { }
            }
            i("Map loaded")
        }
    }
}
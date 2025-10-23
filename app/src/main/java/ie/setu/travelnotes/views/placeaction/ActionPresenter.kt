package ie.setu.travelnotes.views.placeaction

import android.app.Activity
import android.app.DatePickerDialog
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
//            do not work with API 33+ (Android 13+)
//            travelPlace = view.intent.extras?.getParcelable("place_edit")!!
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
            travelPlace.title = "My Place"
            travelPlace.description ="Nice place"
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
        travelPlace.title = view.binding.travelPlaceTitle.text.toString()
        travelPlace.description = view.binding.travelPlaceDescription.text.toString()
        travelPlace.date = LocalDate.parse(view.binding.travelPlaceDate.text.toString())
        if (edit) {
            app.travelPlaces.update(travelPlace)
        } else {
            app.travelPlaces.create(travelPlace)
        }
        i("add or save pressed")
        view.setResult(Activity.RESULT_OK)
        view.finish()
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
                        //val location = result.data!!.extras?.getParcelable<Location>("location")!!
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
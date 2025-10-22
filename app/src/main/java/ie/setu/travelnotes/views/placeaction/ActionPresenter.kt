package ie.setu.travelnotes.views.placeaction

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import ie.setu.travelnotes.main.MainApp
import ie.setu.travelnotes.models.place.PlaceModel
import timber.log.Timber.i
import java.time.LocalDate

class ActionPresenter(private val view: ActionView) {

    var travelPlace = PlaceModel()
    var app: MainApp = view.application as MainApp
    private lateinit var imageIntentLauncher : ActivityResultLauncher<PickVisualMediaRequest>
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
    }

    fun doSelectImage() {
        i("Select image")
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()
        imageIntentLauncher.launch(request)
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
}
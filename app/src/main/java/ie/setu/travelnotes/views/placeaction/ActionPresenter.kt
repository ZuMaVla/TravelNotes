package ie.setu.travelnotes.views.placeaction

import android.app.Activity
import android.content.Intent
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

    fun doSelectImage() {
        i("Select image")
        val request = PickVisualMediaRequest.Builder()
            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
            .build()
        imageIntentLauncher.launch(request)
    }

    init {
        registerImagePickerCallback()
    }

    fun doAddOrSave() {
        travelPlace.title = view.binding.travelPlaceTitle.text.toString()
        travelPlace.description = view.binding.travelPlaceDescription.text.toString()
        travelPlace.date = LocalDate.parse(view.binding.travelPlaceDate.text.toString())

        app.travelPlaces.create(travelPlace)
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
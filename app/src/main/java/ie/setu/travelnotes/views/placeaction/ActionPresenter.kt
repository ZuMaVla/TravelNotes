package ie.setu.travelnotes.views.placeaction

import android.app.Activity
import ie.setu.travelnotes.main.MainApp
import ie.setu.travelnotes.models.place.PlaceModel
import timber.log.Timber.i
import java.time.LocalDate

class ActionPresenter(private val view: ActionView) {

    var travelPlace = PlaceModel()
    var app: MainApp = view.application as MainApp

    fun doAddOrSave() {
        travelPlace.title = view.binding.travelPlaceTitle.text.toString()
        travelPlace.description = view.binding.travelPlaceDescription.text.toString()
        travelPlace.date = LocalDate.parse(view.binding.travelPlaceDate.text.toString())


        app.travelPlaces.create(travelPlace)
        i("add or save pressed")
        view.setResult(Activity.RESULT_OK)
        view.finish()
    }
}
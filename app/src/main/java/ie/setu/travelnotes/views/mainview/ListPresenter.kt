package ie.setu.travelnotes.views.mainview

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import ie.setu.travelnotes.models.place.PlaceModel
import ie.setu.travelnotes.views.map.MapView
import ie.setu.travelnotes.views.placeaction.ActionView
import ie.setu.travelnotes.views.placedetails.PlaceView
import timber.log.Timber.i

class ListPresenter(val view: ListView) {
    
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    init {
        registerRefreshCallback()
        registerMapCallback()
    }

    fun doAddPlace() {
        i("add button pressed")
        val launcherIntent = Intent(view, ActionView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doShowMap() {
        i("Show Map button pressed")
        val launcherIntent = Intent(view, MapView::class.java)
        launcherIntent.putParcelableArrayListExtra("places", ArrayList(getTravelPlaces()))
        mapIntentLauncher.launch(launcherIntent)
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    selectedPosition = RecyclerView.NO_POSITION
                    setMenuStandard()
                    view.onRefresh()
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { }
    }

    fun doPlaceClick(position: Int) {
        selectedPosition = RecyclerView.NO_POSITION
        setMenuStandard()
        view.onRefresh()
        doOpenPlace(position)
    }

    fun doPlaceLongClick(position: Int) {
        if (selectedPosition == position) {
            selectedPosition = RecyclerView.NO_POSITION
            setMenuStandard()
        }
        else {
            selectedPosition = position
            setMenuPlaceSelected()
        }
        view.onRefresh()
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun getTravelPlaces(): List<PlaceModel> {
        return view.app.travelPlaces.findAll()
    }


    internal fun setMenuStandard() {
        i("Menu checked")
        val isListEmpty = view.app.travelPlaces.findAll().isEmpty()
        view.showMapOption(!isListEmpty)
        view.showEditOption(false)
        view.showDeleteOption(false)
        view.showOpenOption(false)
        view.showAddOption(true)
        view.showLogin(true)
    }

    internal fun setMenuPlaceSelected() {
        i("Menu checked")
        val isListEmpty = view.app.travelPlaces.findAll().isEmpty()
        view.showMapOption(false)
        view.showAddOption(false)
        view.showLogin(false)
        view.showEditOption(true)
        view.showDeleteOption(true)
        view.showOpenOption(true)
    }

    fun doEditPlace() {
        i("edit button pressed")
        val chosenPlace = getTravelPlaces()[selectedPosition]
        val launcherIntent = Intent(view, ActionView::class.java)
        launcherIntent.putExtra("place_edit", chosenPlace)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doDeletePlace() {
        i("delete button pressed")
        val chosenPlace = getTravelPlaces()[selectedPosition]
        if (view.app.travelPlaces.delete(chosenPlace)) {
            view.loadTravelPlaces()
            setMenuStandard()
            selectedPosition = RecyclerView.NO_POSITION
        }
    }

    fun doOpenPlace(position: Int) {
        i("open button pressed")
        val chosenPlace = getTravelPlaces()[position]
        val launcherIntent = Intent(view, PlaceView::class.java)
        launcherIntent.putExtra("details_place", chosenPlace)
        refreshIntentLauncher.launch(launcherIntent)
    }

}
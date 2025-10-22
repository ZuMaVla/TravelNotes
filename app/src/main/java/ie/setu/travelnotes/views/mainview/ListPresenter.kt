package ie.setu.travelnotes.views.mainview

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import ie.setu.travelnotes.models.place.PlaceModel
import ie.setu.travelnotes.views.placeaction.ActionView
import timber.log.Timber.i

class ListPresenter(val view: ListView) {
    
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    init {
        registerRefreshCallback()
    }

    fun doAddPlace() {
        i("add button pressed")
        val launcherIntent = Intent(view, ActionView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
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

    fun doPlaceClick(position: Int) {
        selectedPosition = RecyclerView.NO_POSITION
        val place = getTravelPlaces()[position]
        i("place clicked: ${place.title}")
        // In the future, you can launch a details/edit screen here.
        setMenuStandard()
        view.onRefresh()
    }

    fun doPlaceLongClick(position: Int) {
        selectedPosition = position
        i("place LONG clicked and selected: ${getTravelPlaces()[position].title}")
        // Tell the view to refresh the list to apply the highlight
        setMenuPlaceSelected()
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

}
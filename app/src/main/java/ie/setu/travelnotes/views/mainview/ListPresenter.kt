package ie.setu.travelnotes.views.mainview

import android.app.Activity
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import ie.setu.travelnotes.models.place.PlaceModel
import ie.setu.travelnotes.views.login.LoginView
import ie.setu.travelnotes.views.map.MapView
import ie.setu.travelnotes.views.placeaction.ActionView
import ie.setu.travelnotes.views.placedetails.PlaceView
import timber.log.Timber.i

class ListPresenter(val view: ListView) {
    
    private lateinit var refreshIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var mapIntentLauncher : ActivityResultLauncher<Intent>
    private lateinit var loginIntentLauncher : ActivityResultLauncher<Intent>
    var travelPlaces = ArrayList<PlaceModel>()
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    init {
        loadPlaces()
        registerRefreshCallback()
        registerMapCallback()
        registerLoginCallback()
    }

    private fun loadPlaces() {
        val currentUser = view.app.currentUser
        if (currentUser != null) {
            travelPlaces.clear()
            travelPlaces.addAll(view.app.travelPlaces.findAllPlaces(currentUser.id))
        } else {
            travelPlaces.clear()
        }
    }

    fun doAddPlace() {
        i("add button pressed")
        val launcherIntent = Intent(view, ActionView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doShowMap() {
        i("Show Map button pressed")
        val launcherIntent = Intent(view, MapView::class.java)
        launcherIntent.putParcelableArrayListExtra("places", travelPlaces)
        mapIntentLauncher.launch(launcherIntent)
    }

    fun doLogin() {
        i("login button pressed")
        val launcherIntent = Intent(view, LoginView::class.java)
        loginIntentLauncher.launch(launcherIntent)
    }

    fun doLogout() {
        i("logout button pressed")
        view.app.logout()
        loadPlaces() // This will clear the list
        view.onRefresh()
        setMenuStandard()
    }

    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val operation = result.data?.getStringExtra("operation")
                    loadPlaces() // Sync with the master list

                    if (operation == "add") {
                        view.onPlaceAdded()
                    } else {
                        view.onRefresh(selectedPosition)
                    }

                    setMenuStandard()
                    selectedPosition = RecyclerView.NO_POSITION // Reset position at the end
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { }
    }

    private fun registerLoginCallback() {
        loginIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    loadPlaces()
                    view.onRefresh()
                    setMenuStandard()
                }
            }
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
        return travelPlaces
    }

    internal fun setMenuStandard() {
        i("Menu checked")
        if (view.app.currentUser != null) {
            // Logged in state
            val isListEmpty = travelPlaces.isEmpty()
            view.showMapOption(!isListEmpty)
            view.showAddOption(true)
            view.showEditOption(false)
            view.showDeleteOption(false)
            view.showOpenOption(false)
            view.showLoginOption(false)
            view.showLogoutOption(true)
        } else {
            // Logged out state
            view.showMapOption(false)
            view.showAddOption(false)
            view.showEditOption(false)
            view.showDeleteOption(false)
            view.showOpenOption(false)
            view.showLoginOption(true)
            view.showLogoutOption(false)
        }
    }

    internal fun setMenuPlaceSelected() {
        i("Menu checked")
        view.showMapOption(false)
        view.showAddOption(false)
        view.showLoginOption(false)
        view.showLogoutOption(false)
        view.showEditOption(true)
        view.showDeleteOption(true)
        view.showOpenOption(true)
    }

    fun doEditPlace() {
        i("edit button pressed")
        val chosenPlace = travelPlaces[selectedPosition]
        val launcherIntent = Intent(view, ActionView::class.java)
        launcherIntent.putExtra("place_edit", chosenPlace)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doDeletePlace() {
        i("delete button pressed")
        val currentUser = view.app.currentUser
        if (currentUser != null) {
            val chosenPlace = travelPlaces[selectedPosition]
            view.app.travelPlaces.deletePlace(currentUser.id, chosenPlace)
            loadPlaces()
            view.onPlaceDeleted(selectedPosition)
            setMenuStandard()
            selectedPosition = RecyclerView.NO_POSITION
        }
    }

    fun doOpenPlace(position: Int) {
        i("open button pressed")
        val chosenPlace = travelPlaces[position]
        val launcherIntent = Intent(view, PlaceView::class.java)
        launcherIntent.putExtra("details_place", chosenPlace)
        refreshIntentLauncher.launch(launcherIntent)
    }

}
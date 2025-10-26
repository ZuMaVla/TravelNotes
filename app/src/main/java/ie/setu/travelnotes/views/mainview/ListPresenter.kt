package ie.setu.travelnotes.views.mainview

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ie.setu.travelnotes.R
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
    private var filteredPlaces = ArrayList<PlaceModel>()
    private var selectedPosition: Int = RecyclerView.NO_POSITION
    private var isFiltered = false

    init {
        loadPlaces()
        registerRefreshCallback()
        registerMapCallback()
        registerLoginCallback()
    }
    fun getFilteredPlaces(): List<PlaceModel> {
        return filteredPlaces
    }

    private fun loadPlaces() {
        isFiltered = false // Reset filter on load
        val currentUser = view.app.currentUser
        if (currentUser != null) {
            travelPlaces.clear()
            travelPlaces.addAll(view.app.travelPlaces.findAllPlaces(currentUser.id))
        } else {
            travelPlaces.clear()
            filteredPlaces.clear()
        }
    }

    fun doAddPlace() {
        i("add button pressed")
        val launcherIntent = Intent(view, ActionView::class.java)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doShowMap() {
        i("Show Map button pressed")
        val listToShow = if (isFiltered) ArrayList(filteredPlaces) else travelPlaces
        val launcherIntent = Intent(view, MapView::class.java)
        launcherIntent.putParcelableArrayListExtra("places", listToShow)
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
        loadPlaces() // This will clear the list and reset the filter flag
        view.onRefresh()
        refreshMenu()
    }

    fun doFilterSort(anchor: View) {
        i("Filter/Sort FAB pressed")
        val popup = PopupMenu(anchor.context, anchor)
        popup.menuInflater.inflate(R.menu.filter_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_top5 -> filteredPlaces = ArrayList(getTop5())
                R.id.filter_lowest5 -> filteredPlaces = ArrayList(getLowest5())
                R.id.sort_date_asc -> filteredPlaces = ArrayList(sortByDateAscending())
                R.id.sort_date_desc -> filteredPlaces = ArrayList(sortByDateDescending())
                R.id.sort_rating_asc -> filteredPlaces = ArrayList(sortByRatingAscending())
                R.id.sort_rating_desc -> filteredPlaces = ArrayList(sortByRatingDescending())
            }
            
            isFiltered = item.itemId != R.id.show_all

            if (isFiltered) {
                view.showFilteredList()
            } else {
                view.showFullList()
            }
            refreshMenu()
            true
        }
        popup.show()
    }
    private fun registerRefreshCallback() {
        refreshIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val operation = result.data?.getStringExtra("operation")
                    
                    if (operation == "add") {
                        loadPlaces()
                        view.onPlaceAdded()
                    } else if (operation == "edit") {
                        val editedPlace = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                            result.data?.getParcelableExtra("place_edited", PlaceModel::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            result.data?.getParcelableExtra<PlaceModel>("place_edited")
                        }
                        if (editedPlace != null) {
                            // Efficiently update the local lists
                            val indexInMaster = travelPlaces.indexOfFirst { it.id == editedPlace.id }
                            if (indexInMaster != -1) {
                                travelPlaces[indexInMaster] = editedPlace
                            }
                            if (isFiltered) {
                                val indexInFiltered = filteredPlaces.indexOfFirst { it.id == editedPlace.id }
                                if (indexInFiltered != -1) {
                                    filteredPlaces[indexInFiltered] = editedPlace
                                }
                            }
                            view.onRefresh(selectedPosition)
                        }
                    }
                    
                    refreshMenu()
                    selectedPosition = RecyclerView.NO_POSITION // Reset position at the end
                }
            }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                 if (result.resultCode == Activity.RESULT_OK) {
                    loadPlaces()
                    view.onRefresh()
                }
            }
    }

    private fun registerLoginCallback() {
        loginIntentLauncher =
            view.registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    isFiltered = false
                    loadPlaces()
                    view.showFullList()
                    view.onRefresh()
                    refreshMenu()
                }
            }
    }

    fun doPlaceClick(position: Int) {
        // A click can either open a place for details, or clear a selection
        if (selectedPosition == RecyclerView.NO_POSITION) {
            selectedPosition = position
            doOpenPlace(position)
        } else {
            selectedPosition = RecyclerView.NO_POSITION
            refreshMenu()
            view.onRefresh()
        }
    }

    fun doPlaceLongClick(position: Int) {
        if (!isFiltered) { // Only allow contextual menu if not filtered
            if (selectedPosition == position) {
                selectedPosition = RecyclerView.NO_POSITION
                refreshMenu()
            } else {
                selectedPosition = position
                setMenuPlaceSelected()
            }
            view.onRefresh()
        }
    }

    fun getSelectedPosition(): Int {
        return selectedPosition
    }

    fun getTravelPlaces(): List<PlaceModel> {
        return travelPlaces
    }
    
    fun refreshMenu() {
        setMenuStandard()
    }

    fun setMenuStandard() {
        i("Menu checked")
        if (view.app.currentUser != null) {
            // Logged in state
            val isListEmpty = travelPlaces.isEmpty()
            view.showMapOption(!isListEmpty)
            view.showFilterSortOption(!isListEmpty)
            view.showAddOption(!isFiltered) // Disable add when filtered
            view.showEditOption(false)
            view.showDeleteOption(false)
            view.showOpenOption(false)
            view.showLoginOption(false)
            view.showLogoutOption(true)
        } else {
            // Logged out state
            view.showMapOption(false)
            view.showFilterSortOption(false)
            view.showAddOption(false)
            view.showEditOption(false)
            view.showDeleteOption(false)
            view.showOpenOption(false)
            view.showLoginOption(true)
            view.showLogoutOption(false)
        }
    }

    private fun setMenuPlaceSelected() {
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
        val chosenPlace = if (isFiltered) filteredPlaces[selectedPosition] else travelPlaces[selectedPosition]
        val launcherIntent = Intent(view, ActionView::class.java)
        launcherIntent.putExtra("place_edit", chosenPlace)
        refreshIntentLauncher.launch(launcherIntent)
    }

    fun doDeletePlace() {
        i("delete button pressed")
        val currentUser = view.app.currentUser
        if (currentUser != null) {
            val chosenPlace = if (isFiltered) filteredPlaces[selectedPosition] else travelPlaces[selectedPosition]
            view.app.travelPlaces.deletePlace(currentUser.id, chosenPlace)
            val positionToDelete = selectedPosition
            loadPlaces()
            view.onPlaceDeleted(positionToDelete)
            refreshMenu()
            selectedPosition = RecyclerView.NO_POSITION
        }
    }

    fun doOpenPlace(position: Int) {
        i("open button pressed")
        val chosenPlace = if (isFiltered) filteredPlaces[position] else travelPlaces[position]
        val launcherIntent = Intent(view, PlaceView::class.java)
        launcherIntent.putExtra("details_place", chosenPlace)
        refreshIntentLauncher.launch(launcherIntent)
    }
    private fun getTop5() = travelPlaces.sortedByDescending { it.rating }.take(5)
    private fun getLowest5() = travelPlaces.sortedBy { it.rating }.take(5)
    private fun sortByDateAscending() = travelPlaces.sortedBy { it.date }
    private fun sortByDateDescending() = travelPlaces.sortedByDescending { it.date }
    private fun sortByRatingAscending() = travelPlaces.sortedBy { it.rating }
    private fun sortByRatingDescending() = travelPlaces.sortedByDescending { it.rating }

}
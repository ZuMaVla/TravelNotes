package ie.setu.travelnotes.views.mainview

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ie.setu.travelnotes.R
import ie.setu.travelnotes.databinding.MainPageBinding
import ie.setu.travelnotes.main.MainApp

class ListView : AppCompatActivity(), PlaceListener {

    lateinit var app: MainApp
    lateinit var binding: MainPageBinding
    private lateinit var presenter: ListPresenter
    private lateinit var adapterListToDisplay: ListAdapter
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        app = application as MainApp
        presenter = ListPresenter(this)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPlaces.layoutManager = layoutManager

        // Create adapter ONCE and pass it the initial list
        adapterListToDisplay = ListAdapter(presenter.getTravelPlaces(), this, presenter)
        binding.recyclerViewPlaces.adapter = adapterListToDisplay

        binding.fabFilterSort.setOnClickListener {
            presenter.doFilterSort(binding.fabFilterSort as View)
        }
    }

    fun showFilteredList() {
        adapterListToDisplay.updateData(presenter.getFilteredPlaces())
    }

    fun showFullList() {
        adapterListToDisplay.updateData(presenter.getTravelPlaces())
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.main_menu, menu)
        presenter.setMenuStandard()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> { presenter.doAddPlace() }
            R.id.item_edit -> { presenter.doEditPlace() }
            R.id.item_delete -> { presenter.doDeletePlace() }
            R.id.item_map -> { presenter.doShowMap() }
            R.id.item_open -> { presenter.doOpenPlace(presenter.getSelectedPosition()) }
            R.id.item_login -> { presenter.doLogin() }
            R.id.item_logout -> { presenter.doLogout() }
        }
        return super.onOptionsItemSelected(item)
    }

    // For general refreshes (e.g. highlight changes)
    fun onRefresh() {
        adapterListToDisplay.notifyDataSetChanged()
    }

    // For targeted edit refreshes
    fun onRefresh(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            adapterListToDisplay.notifyItemChanged(position)
        }
    }

    fun onPlaceAdded() {
        adapterListToDisplay.notifyItemInserted(presenter.getTravelPlaces().size - 1)
    }

    fun onPlaceDeleted(position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            adapterListToDisplay.notifyItemRemoved(position)
        }
    }

    fun showLoginOption(show: Boolean) {
        menu?.findItem(R.id.item_login)?.isVisible = show
    }
    
    fun showLogoutOption(show: Boolean) {
        menu?.findItem(R.id.item_logout)?.isVisible = show
    }

    fun showMapOption(show: Boolean) {
        menu?.findItem(R.id.item_map)?.isVisible = show
    }

    fun showEditOption(show: Boolean) {
        menu?.findItem(R.id.item_edit)?.isVisible = show
    }

    fun showDeleteOption(show: Boolean) {
        menu?.findItem(R.id.item_delete)?.isVisible = show
    }

    fun showOpenOption(show: Boolean) {
        menu?.findItem(R.id.item_open)?.isVisible = show
    }

    fun showAddOption(show: Boolean) {
        menu?.findItem(R.id.item_add)?.isVisible = show
    }

    fun showFilterSortOption(show: Boolean) {
        if (show) binding.fabFilterSort.show() else binding.fabFilterSort.hide()
    }

    override fun onPlaceClick(position: Int) {
        presenter.doPlaceClick(position)
    }

    override fun onPlaceLongClick(position: Int) {
        presenter.doPlaceLongClick(position)
    }
}
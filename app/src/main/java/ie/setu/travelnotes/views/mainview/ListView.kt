package ie.setu.travelnotes.views.mainview

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.travelnotes.R
import ie.setu.travelnotes.databinding.MainPageBinding
import ie.setu.travelnotes.main.MainApp

class ListView : AppCompatActivity(), PlaceListener {

    lateinit var app: MainApp
    private lateinit var binding: MainPageBinding
    private lateinit var presenter: ListPresenter
    var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainPageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        presenter = ListPresenter(this)
        app = application as MainApp
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewPlaces.layoutManager = layoutManager
        loadTravelPlaces()
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
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadTravelPlaces() {
        binding.recyclerViewPlaces.adapter = ListAdapter(presenter.getTravelPlaces(), this, presenter)
        onRefresh()
    }

    fun onRefresh() {
        binding.recyclerViewPlaces.adapter?.
        notifyItemRangeChanged(0,presenter.getTravelPlaces().size)
    }

    fun showLogin(show: Boolean) {
        menu?.findItem(R.id.item_login)?.isVisible = show
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

    override fun onPlaceClick(position: Int) {
        presenter.doPlaceClick(position)
    }

    override fun onPlaceLongClick(position: Int) {
        presenter.doPlaceLongClick(position)
    }
}
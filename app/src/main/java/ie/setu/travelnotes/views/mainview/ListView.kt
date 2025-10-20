package ie.setu.travelnotes.views.mainview

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ie.setu.travelnotes.R
import ie.setu.travelnotes.databinding.MainPageBinding
import ie.setu.travelnotes.main.MainApp
import timber.log.Timber.i

class ListView : AppCompatActivity() {

    lateinit var app: MainApp
    private lateinit var binding: MainPageBinding
    private lateinit var presenter: ListPresenter




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
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> { presenter.doAddPlace() }
//            R.id.item_map -> { presenter.doShowMap() }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun loadTravelPlaces() {
        binding.recyclerViewPlaces.adapter = ListAdapter(presenter.getTravelPlaces())
        onRefresh()
    }

    fun onRefresh() {
        binding.recyclerViewPlaces.adapter?.
        notifyItemRangeChanged(0,presenter.getTravelPlaces().size)
    }
}
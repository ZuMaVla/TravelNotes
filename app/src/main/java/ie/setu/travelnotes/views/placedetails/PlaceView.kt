package ie.setu.travelnotes.views.placedetails

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.SupportMapFragment
import com.squareup.picasso.Picasso
import ie.setu.travelnotes.R
import ie.setu.travelnotes.databinding.DetailsPlaceBinding
import ie.setu.travelnotes.models.place.PlaceModel

class PlaceView : AppCompatActivity(), CommentListener {

    private lateinit var binding: DetailsPlaceBinding
    private lateinit var presenter: PlacePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DetailsPlaceBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarDetails)
        presenter = PlacePresenter(this)

        binding.commentsRecycler.layoutManager = LinearLayoutManager(this)

        binding.backButton.setOnClickListener {
            binding.commentInput.text.clear()
        }

        binding.addCommentButton.setOnClickListener {
            val commentText = binding.commentInput.text.toString()
            if (commentText.isNotEmpty()) {
                presenter.doAddComment(commentText)
            }
        }

        binding.ratingOverlay.setOnClickListener {
            showRatingPicker()
        }
    }

    private fun showRatingPicker() {
        val numberPicker = NumberPicker(this)
        numberPicker.minValue = 1
        numberPicker.maxValue = 10
        numberPicker.value = binding.ratingValue.text.toString().toIntOrNull() ?: 1

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Rating")
        builder.setView(numberPicker)
        builder.setPositiveButton("OK") { _, _ ->
            presenter.doSetRating(numberPicker.value)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.details_place_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_return -> {
                val resultIntent = Intent()
                resultIntent.putExtra("operation", "edit")
                resultIntent.putExtra("place_edited", presenter.travelPlace.copy()) // Use .copy()
                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showPlaceDetails(place: PlaceModel) {
        binding.placeTitle.text = place.title
        binding.placeDescription.text = place.description

        if (place.image != Uri.EMPTY) {
            Picasso.get().load(place.image).into(binding.placeImage)
        }
        setupMap()
        binding.ratingValue.text = place.rating.toString()

        binding.commentsRecycler.adapter = CommentAdapter(place.comments, this)
    }

    fun onCommentAdded() {
        hideKeyboard()
        binding.commentInput.text.clear()
        binding.commentsRecycler.adapter?.notifyItemInserted(presenter.getComments().size)
    }

    fun onCommentDeleted(position: Int) {
        binding.commentsRecycler.adapter?.notifyItemRemoved(position)
    }

    fun onRatingSet(rating: Double) {
        binding.ratingValue.text = rating.toString()
    }

    override fun onDeleteCommentClick(position: Int) {
        presenter.doDeleteComment(position)
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapContainer) as SupportMapFragment

        mapFragment.getMapAsync { googleMap ->
            presenter.configureMap(this, googleMap)
        }
    }

}
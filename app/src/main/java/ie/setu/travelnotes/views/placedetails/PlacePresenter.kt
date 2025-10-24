package ie.setu.travelnotes.views.placedetails

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.travelnotes.main.MainApp
import ie.setu.travelnotes.models.place.CommentModel
import ie.setu.travelnotes.models.place.PlaceModel


class PlacePresenter(val view: PlaceView) {
    private var travelPlace = PlaceModel()
    var app: MainApp = view.application as MainApp
    init {
        travelPlace = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            view.intent.getParcelableExtra("details_place", PlaceModel::class.java)!!
        } else {
            @Suppress("DEPRECATION")
            view.intent.getParcelableExtra<PlaceModel>("details_place")!!
        }
        view.showPlaceDetails(travelPlace)
    }

    fun getComments(): ArrayList<CommentModel> = travelPlace.comments

    fun doAddComment(commentText: String) {
        val newComment = CommentModel(author = "Author", text = commentText)
        travelPlace.comments.add(newComment)
        app.travelPlaces.update(travelPlace)
        view.onCommentAdded()
    }

    fun doDeleteComment(position: Int) {
        travelPlace.comments.removeAt(position)
        app.travelPlaces.update(travelPlace)
        view.onCommentDeleted(position)
    }

    fun doSetRating(rating: Int) {
        travelPlace.rating = rating.toDouble()
        app.travelPlaces.update(travelPlace)
        view.onRatingSet(travelPlace.rating)
    }

    fun configureMap(view: PlaceView, googleMap: GoogleMap) {
        val location = LatLng(travelPlace.lat, travelPlace.lng)
        googleMap.addMarker(
            MarkerOptions()
                .position(location)
                .title(travelPlace.title)
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }
}
package ie.setu.travelnotes.views.map

import android.app.Activity
import android.content.Intent
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import ie.setu.travelnotes.models.place.PlaceModel
import ie.setu.travelnotes.views.placedetails.PlaceView

class MapPresenter(private val view: MapView) {

    private var places = ArrayList<PlaceModel>()
    private var lat: Double = 0.0
    private var lng: Double = 0.0
    private var zoom: Float = 0f
    var mode = "one"

    init {
        if (view.intent.hasExtra("places")) {
            places = view.intent.extras?.getParcelableArrayList("places")!!
            mode = "all"
        }
        else {
            lat = view.intent.getDoubleExtra("lat", 0.0)
            lng = view.intent.getDoubleExtra("lng", 0.0)
            zoom = view.intent.getFloatExtra("zoom", 15f)
        }
    }

    fun doConfigureMap(mMap: GoogleMap) {
        if (mode == "all") {
            val boundsBuilder = LatLngBounds.Builder()
            places.forEach { 
                val loc = LatLng(it.lat, it.lng)
                val options = MarkerOptions().title(it.title).position(loc)
                val marker = mMap.addMarker(options)
                marker?.tag = it.id // Tag with ID
                boundsBuilder.include(loc)
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
        }
        else {
            val location = LatLng(lat, lng)
            val options = MarkerOptions()
                .title("Travel Place")
                .snippet("GPS : $location")
                .draggable(true)
                .position(location)
            mMap.addMarker(options)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
        }
    }

    fun doInfoWindowClick(marker: Marker) {
        val placeId = marker.tag as Long
        val place = places.find { it.id == placeId }
        if (place != null) {
            val intent = Intent(view, PlaceView::class.java)
            intent.putExtra("details_place", place)
            view.startActivity(intent)
        }
    }

    fun doMarkerDrag(marker: Marker) {
        if (mode == "one") {
            lat = marker.position.latitude
            lng = marker.position.longitude
        }
    }

    fun doSave() {
        if (mode == "one") {
            val resultIntent = Intent()
            resultIntent.putExtra("lat", lat)
            resultIntent.putExtra("lng", lng)
            view.setResult(Activity.RESULT_OK, resultIntent)
        }
        view.finish()
    }

    fun doCancel() {
        view.setResult(Activity.RESULT_OK) // Always return OK to trigger refresh
        view.finish()
    }
}
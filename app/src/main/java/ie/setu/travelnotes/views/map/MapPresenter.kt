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
            // Show All Travel Places Mode
            val boundsBuilder = LatLngBounds.Builder()
            places.forEach {
                val loc = LatLng(it.lat, it.lng)
                val options = MarkerOptions().title(it.title).position(loc)
                mMap.addMarker(options)
                boundsBuilder.include(loc)
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(boundsBuilder.build(), 100))
        }
        else {
            // Single Travel Place Mode; mode remains default ("one")
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

    fun doMarkerDrag(marker: Marker) {
        if (mode == "one") { // Only allow drag in single location mode
            lat = marker.position.latitude
            lng = marker.position.longitude
        }
    }

    fun doSave() {
        if (mode == "one") { // Only save in single location mode
            val resultIntent = Intent()
            resultIntent.putExtra("lat", lat)
            resultIntent.putExtra("lng", lng)
            view.setResult(Activity.RESULT_OK, resultIntent)
        }
        view.finish()
    }

    fun doCancel() {
        view.finish()
    }
}
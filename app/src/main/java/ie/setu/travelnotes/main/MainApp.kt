package ie.setu.travelnotes.main

import android.app.Application
import ie.setu.travelnotes.models.place.PlaceJSONStore
import ie.setu.travelnotes.models.place.PlaceMemStore
import ie.setu.travelnotes.models.place.PlaceStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var travelPlaces: PlaceStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
//        travelPlaces = PlaceMemStore()
        travelPlaces = PlaceJSONStore(applicationContext)
        i("App started")
    }
}
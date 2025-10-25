package ie.setu.travelnotes.main

import android.app.Application
import ie.setu.travelnotes.models.store.JSONStore
import ie.setu.travelnotes.models.place.PlaceStore
import ie.setu.travelnotes.models.user.UserModel
import ie.setu.travelnotes.models.user.UserStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var travelPlaces: PlaceStore
    lateinit var users: UserStore
    var currentUser: UserModel? = null

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        val dataStore = JSONStore(applicationContext)
        travelPlaces = dataStore
        users = dataStore
        i("App started with unified JSON Store")
    }

    fun logout() {
        currentUser = null
        i("User Logged Out")
    }
}
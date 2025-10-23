package ie.setu.travelnotes.models.place

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class PlaceMemStore  : PlaceStore {

    val travelPlaces = ArrayList<PlaceModel>()

    override fun findAll(): List<PlaceModel> {
        return travelPlaces
    }
    override fun findById(id:Long) : PlaceModel?{
        val foundPlace: PlaceModel? = travelPlaces.find { it.id == id }
        return foundPlace
    }
    override fun create(travelPlace: PlaceModel){
        travelPlace.id = getId()
        travelPlaces.add(travelPlace)
        logAll()
    }
    override fun update(travelPlace: PlaceModel){
        var foundPlace: PlaceModel? = travelPlaces.find { p -> p.id == travelPlace.id }
        if (foundPlace != null) {
            foundPlace.title = travelPlace.title
            foundPlace.description = travelPlace.description
            foundPlace.image = travelPlace.image
            foundPlace.date = travelPlace.date
        }
    }
    override fun delete(travelPlace: PlaceModel) : Boolean {
        val size = travelPlaces.size
        travelPlaces.remove(travelPlace)
        if (travelPlaces.size != size - 1) {
            return false
        } else {
            return true
        }
    }
    private fun logAll() {
        travelPlaces.forEach{ i("${it}") }
    }
}
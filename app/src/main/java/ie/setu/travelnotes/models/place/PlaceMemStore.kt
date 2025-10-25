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
    override fun update(travelPlace: PlaceModel) {
        val index = travelPlaces.indexOfFirst { it.id == travelPlace.id }
        if (index != -1) {
            travelPlaces[index] = travelPlace
        }
    }
    override fun delete(travelPlace: PlaceModel) : Boolean {
        val size = travelPlaces.size
        travelPlaces.remove(travelPlace)
        return size == travelPlaces.size + 1
    }
    private fun logAll() {
        travelPlaces.forEach{ i("${it}") }
    }
}
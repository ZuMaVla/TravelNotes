package ie.setu.travelnotes.models.place

class PlaceMemStore  : PlaceStore {

    val travelPlaces = ArrayList<PlaceModel>()

    override fun findAll(): List<PlaceModel> {
        return travelPlaces
    }
    override fun findById(id:Long) : PlaceModel?{
        return travelPlaces[0]
    }
    override fun create(travelPlace: PlaceModel){
        travelPlaces.add(travelPlace)
    }
    override fun update(travelPlace: PlaceModel){
        travelPlaces[0] = travelPlace
    }
    override fun delete(travelPlace: PlaceModel){
        travelPlaces.remove(travelPlace)
    }
}
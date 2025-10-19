package ie.setu.travelnotes.models.place

interface PlaceStore {
    fun findAll(): List<PlaceModel>
    fun findById(id:Long) : PlaceModel?
    fun create(travelPlace: PlaceModel)
    fun update(travelPlace: PlaceModel)
    fun delete(travelPlace: PlaceModel)

}
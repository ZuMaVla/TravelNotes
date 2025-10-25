package ie.setu.travelnotes.models.place

interface PlaceStore {
    fun findAllPlaces(userId: Long): List<PlaceModel>
    fun findPlaceById(userId: Long, placeId: Long): PlaceModel?
    fun createPlace(place: PlaceModel)
    fun updatePlace(userId: Long, place: PlaceModel)
    fun deletePlace(userId: Long, place: PlaceModel)
}
package ie.setu.travelnotes.models.place

import java.time.LocalDate

data class PlaceModel(var id: Long = 0,
                      var title: String = "",
                      var description: String = "",
                      var date: LocalDate = LocalDate.now()


)
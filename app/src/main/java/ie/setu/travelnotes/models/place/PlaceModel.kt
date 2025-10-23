package ie.setu.travelnotes.models.place

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate

@Parcelize
data class PlaceModel(var id: Long = 0,
                      var title: String = "",
                      var description: String = "",
                      var date: LocalDate = LocalDate.now(),
                      var image: Uri = Uri.EMPTY,
                      var lat: Double = 51.8985,
                      var lng: Double = -8.4756
) : Parcelable
package ie.setu.travelnotes.models.user

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserModel(
    var id: Long = 0,
    var name: String = "",
    var password: String = ""
) : Parcelable
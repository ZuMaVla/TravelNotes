package ie.setu.travelnotes.models.store

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import com.google.gson.*
import ie.setu.travelnotes.helpers.exists
import ie.setu.travelnotes.helpers.read
import ie.setu.travelnotes.helpers.write
import ie.setu.travelnotes.models.place.PlaceModel
import ie.setu.travelnotes.models.place.PlaceStore
import ie.setu.travelnotes.models.user.UserModel
import ie.setu.travelnotes.models.user.UserStore
import timber.log.Timber
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime

private const val JSON_FILE = "travel_notes_.json"

// --- Data Containers ---
data class JsonContainer(
    var places: ArrayList<PlaceModel> = ArrayList(),
    var users: ArrayList<UserModel> = ArrayList()
)

// --- UNIFIED JSON STORE ---
class JSONStore(private val context: Context) : PlaceStore, UserStore {

    var travelPlaces = ArrayList<PlaceModel>()
    var users = ArrayList<UserModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }


    override fun findAllPlaces(userId: Long): List<PlaceModel> {
        return travelPlaces.filter { it.userId == userId }
    }

    override fun findPlaceById(userId: Long, placeId: Long): PlaceModel? {
        return travelPlaces.find { it.userId == userId && it.id == placeId }
    }

    override fun createPlace(place: PlaceModel) {
        place.id = System.currentTimeMillis()
        travelPlaces.add(place)
        serialize()
    }

    override fun updatePlace(userId: Long, place: PlaceModel) {
        val foundPlace = findPlaceById(userId, place.id)
        if (foundPlace != null) {
            val index = travelPlaces.indexOfFirst { it.id == place.id }
            if (index != -1) {
                travelPlaces[index] = place
                serialize()
            }
        }
    }

    override fun deletePlace(userId: Long, place: PlaceModel) {
        val foundPlace = findPlaceById(userId, place.id)
        if (foundPlace != null) {
            travelPlaces.remove(place)
            serialize()
        }
    }

    // UserStore Implementation
    override fun findAllUsers(): List<UserModel> {
        return users
    }

    override fun findUserById(id: Long): UserModel? {
        return users.find { it.id == id }
    }

    override fun findUserByName(name: String): UserModel? {
        return users.find { it.name == name }
    }

    override fun createUser(user: UserModel) {
        user.id = System.currentTimeMillis()
        users.add(user)
        serialize()
    }

    override fun updateUser(user: UserModel) {
        val index = users.indexOfFirst { it.id == user.id }
        if (index != -1) {
            users[index] = user
            serialize()
        }
    }

    override fun deleteUser(user: UserModel) {
        users.remove(user)
        serialize()
    }

    // --- SERIALIZATION ---
    private fun serialize() {
        val gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create()
        val jsonContainer = JsonContainer(travelPlaces, users)
        val jsonString = gson.toJson(jsonContainer)
        Timber.i("JSON Output: \n$jsonString")
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriAdapter())
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()
        val jsonString = read(context, JSON_FILE)
        val container = gson.fromJson(jsonString, JsonContainer::class.java)
        if (container != null) {
            travelPlaces = container.places
            users = container.users
        }
    }
}

// --- GSON ADAPTERS ---
class UriAdapter : JsonDeserializer<Uri>,JsonSerializer<Uri> { 
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): Uri {
        return json?.asString?.toUri() ?: Uri.EMPTY
    }
    override fun serialize(src: Uri?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    override fun serialize(src: LocalDate?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.toString())
    }
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDate {
        return json?.asString?.let { LocalDate.parse(it) } ?: LocalDate.now()
    }
}

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    override fun serialize(src: LocalDateTime?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        return JsonPrimitive(src?.toString())
    }
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): LocalDateTime {
        return json?.asString?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
    }
}
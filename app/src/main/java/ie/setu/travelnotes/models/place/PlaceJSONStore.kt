package ie.setu.travelnotes.models.place

import android.content.Context
import android.net.Uri
import com.google.gson.*
import ie.setu.travelnotes.helpers.exists
import ie.setu.travelnotes.helpers.read
import ie.setu.travelnotes.helpers.write
import timber.log.Timber
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.LocalDateTime
import androidx.core.net.toUri

private const val JSON_FILE = "travel_notes.json"

class PlaceJSONStore(private val context: Context) : PlaceStore {

    var travelPlaces = ArrayList<PlaceModel>()

    init {
        if (exists(context, JSON_FILE)) {
            travelPlaces = deserialize()           // read from disk and convert
        }
    }

    override fun findAll(): ArrayList<PlaceModel> {
        return travelPlaces
    }

    override fun findById(id: Long): PlaceModel? {
        return travelPlaces.find { it.id == id }
    }

    override fun create(travelPlace: PlaceModel) {
        travelPlace.id = System.currentTimeMillis() // Generate a unique ID
        travelPlaces.add(travelPlace)
        serialize()
    }

    override fun update(travelPlace: PlaceModel) {
        val index = travelPlaces.indexOfFirst { it.id == travelPlace.id }
        if (index != -1) {
            travelPlaces[index] = travelPlace
            serialize()
        }
    }

    override fun delete(travelPlace: PlaceModel): Boolean {
        val size = travelPlaces.size
        travelPlaces.remove(travelPlace)
        serialize()
        return size == travelPlaces.size + 1
    }

    private fun serialize() {
        val gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriAdapter()) // Use UriAdapter
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .setPrettyPrinting()
            .create()
        val jsonPlaceEmitter = JsonPlaceContainer(travelPlaces)
        val jsonString = gson.toJson(jsonPlaceEmitter)
        Timber.i("JSON Output: \n$jsonString")
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize(): ArrayList<PlaceModel> {
        val gson = GsonBuilder()
            .registerTypeAdapter(Uri::class.java, UriAdapter()) // Use UriAdapter
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()
        val jsonString = read(context, JSON_FILE)
        val jsonPlaceReceiver = gson.fromJson(jsonString, JsonPlaceContainer::class.java)
        return jsonPlaceReceiver?.places ?: ArrayList()
    }

}

// Wrapper class for the root of our JSON file
data class JsonPlaceContainer(
    var places: ArrayList<PlaceModel> = ArrayList()
)

// Custom type adapters for Gson
class UriAdapter : JsonDeserializer<Uri>,JsonSerializer<Uri> { // Renamed from UriParser
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return json?.asString?.toUri() ?: Uri.EMPTY
    }
    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}

class LocalDateAdapter : JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    override fun serialize(
        src: LocalDate?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toString())
    }
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDate {
        return json?.asString?.let { LocalDate.parse(it) } ?: LocalDate.now()
    }
}

class LocalDateTimeAdapter : JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    override fun serialize(
        src: LocalDateTime?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src?.toString())
    }
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return json?.asString?.let { LocalDateTime.parse(it) } ?: LocalDateTime.now()
    }
}

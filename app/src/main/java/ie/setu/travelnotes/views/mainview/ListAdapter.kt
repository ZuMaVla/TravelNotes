package ie.setu.travelnotes.views.mainview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.travelnotes.databinding.CardPlaceBinding
import ie.setu.travelnotes.models.place.PlaceModel

class ListAdapter(private val travelPlaces: List<PlaceModel>)
    : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: CardPlaceBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(travelPlace: PlaceModel) {
            binding.placeTitle.text = travelPlace.title
            binding.placeDescription.text = travelPlace.description
            binding.placeDate.text = travelPlace.date.toString()
            Picasso.get()
                .load(travelPlace.image)
                .into(binding.imageIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPlaceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(travelPlaces[position])
    }

    override fun getItemCount() = travelPlaces.size
}

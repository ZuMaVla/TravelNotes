package ie.setu.travelnotes.views.mainview

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import ie.setu.travelnotes.databinding.CardPlaceBinding
import ie.setu.travelnotes.models.place.PlaceModel

interface PlaceListener {
    fun onPlaceClick(position: Int)
    fun onPlaceLongClick(position: Int)
}

class ListAdapter(
    private val travelPlaces: List<PlaceModel>,
    private val listener: PlaceListener,
    private val presenter: ListPresenter
) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: CardPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPlaceClick(position)
                }
            }
            binding.root.setOnLongClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onPlaceLongClick(position)
                }
                true // Consume the long click
            }
        }

        fun bind(travelPlace: PlaceModel, position: Int) {
            binding.placeTitle.text = travelPlace.title
            binding.placeDescription.text = travelPlace.description
            binding.placeDate.text = travelPlace.date.toString()
            Picasso.get()
                .load(travelPlace.image)
                .into(binding.imageIcon)

            if (position == presenter.getSelectedPosition()) {
                binding.root.setBackgroundColor(Color.LTGRAY)
            } else {
                binding.root.setBackgroundColor(Color.WHITE)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardPlaceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(travelPlaces[position], position) // Correctly pass the position
    }

    override fun getItemCount() = travelPlaces.size
}

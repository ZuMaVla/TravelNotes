package ie.setu.travelnotes.views.mainview

import android.util.TypedValue
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

class ListAdapter(private val travelPlaces: List<PlaceModel>,
                  private val listener: PlaceListener,
                  private val presenter: ListPresenter) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

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

        fun bind(place: PlaceModel, position: Int) {
            binding.placeTitle.text = place.title
            binding.placeDescription.text = place.description
            binding.placeDate.text = place.date.toString()
            binding.placeRating.text = place.rating.toString() // Set the rating text
            Picasso.get()
                .load(place.image)
                .into(binding.imageIcon)

            // Use theme-aware colors
            val highlightColor = TypedValue()
            itemView.context.theme.resolveAttribute(android.R.attr.colorControlHighlight, highlightColor, true)
            val defaultColor = TypedValue()
            itemView.context.theme.resolveAttribute(android.R.attr.colorBackground, defaultColor, true)

            if (position == presenter.getSelectedPosition()) {
                binding.root.setBackgroundColor(highlightColor.data)
            } else {
                binding.root.setBackgroundColor(defaultColor.data)
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
        holder.bind(travelPlaces[position], position)
    }

    override fun getItemCount() = travelPlaces.size
}
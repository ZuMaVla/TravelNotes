package ie.setu.travelnotes.views.placedetails

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.setu.travelnotes.databinding.CardCommentBinding
import ie.setu.travelnotes.models.place.CommentModel
import java.time.format.DateTimeFormatter

interface CommentListener {
    fun onDeleteCommentClick(position: Int)
}

class CommentAdapter(
    private val comments: List<CommentModel>,
    private val listener: CommentListener
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: CardCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentModel, position: Int) {
            binding.commentAuthor.text = comment.author
            binding.commentText.text = comment.text
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            binding.commentDate.text = comment.date.format(formatter)

            binding.deleteCommentButton.setOnClickListener {
                listener.onDeleteCommentClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CardCommentBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(comments[position], position)
    }

    override fun getItemCount() = comments.size
}
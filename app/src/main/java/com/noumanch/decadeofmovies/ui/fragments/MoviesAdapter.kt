package com.noumanch.decadeofmovies.ui.fragments
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noumanch.decadeofmovies.R
import com.noumanch.decadeofmovies.databinding.ItemMovieBinding
import com.noumanch.decadeofmovies.databinding.ItemMovieHeadingBinding
import com.noumanch.decadeofmovies.models.Movie

const val TYPE_HEADING = 232
const val TYPE_MOVIE = 434

class MoviesAdapter(
    private var movies: MutableList<Any>,
    private val movieClickCallback: (Movie) -> Unit,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) = if (p1 == TYPE_HEADING) {
        HeadingViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_movie_heading, parent, false)
        )
    } else {
        MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        if (movies[position] is String) return TYPE_HEADING else if (movies[position] is Movie) return TYPE_MOVIE
        return super.getItemViewType(position)
    }

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        movies[position].let { movie ->
            if (holder is HeadingViewHolder) {
                holder.bind(movie as String)
            } else if (holder is MovieViewHolder) {
                holder.bind(
                    movie as Movie
                )
                holder.binding.itemSelectView.setOnClickListener {
                    movieClickCallback.invoke(movie as Movie)
                }
            }
        }
    }

    class HeadingViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val binding = ItemMovieHeadingBinding.bind(view)
        fun bind(heading: String) {
            binding.heading.text = heading
        }

    }

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMovieBinding.bind(view)
        fun bind(movie: Movie, ) {

        }
    }

    fun update(list: List<Any>) {
        movies.clear()
        movies.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        movies.clear()
        notifyDataSetChanged()
    }
}

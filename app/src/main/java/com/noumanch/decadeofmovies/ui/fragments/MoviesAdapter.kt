package com.noumanch.decadeofmovies.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.noumanch.decadeofmovies.R
import com.noumanch.decadeofmovies.databinding.ItemMovieBinding
import com.noumanch.decadeofmovies.databinding.ItemMovieHeadingBinding
import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.utils.extensions.hide
import com.noumanch.decadeofmovies.utils.extensions.show


class MoviesAdapter(
    private var movies: MutableList<Movie>,
    private val movieClickCallback: (Movie) -> Unit,
    private val updatedSearchQuery: () -> String,
) :
    RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) =
        MovieViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_movie, parent, false)
        )

    override fun getItemCount() = movies.size

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        movies[position].let { movie ->
            holder.bind(movie, position)
            holder.binding.itemSelectView.setOnClickListener {
                movieClickCallback.invoke(movie)
            }
        }
    }

    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMovieBinding.bind(view)
        fun bind(movie: Movie, position: Int) {
            binding.movieTitleTV.text = movie.title
            binding.movieYearTV.text = "${movie.year}"
            binding.movieRating.text = "${movie.rating}"
            binding.heading.text = "${movie.year}"
            if (updatedSearchQuery.invoke().isEmpty())
                binding.heading.hide()
            else {
                when {
                    position == 0 -> binding.heading.show()
                    movie.year != movies[position - 1].year -> binding.heading.show()
                    else -> binding.heading.hide()
                }
            }
        }
    }

    fun update(list: List<Movie>) {
        movies.clear()
        movies.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        movies.clear()
        notifyDataSetChanged()
    }
}

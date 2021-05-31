package com.noumanch.decadeofmovies.ui.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.noumanch.decadeofmovies.R
import com.noumanch.decadeofmovies.databinding.ItemMovieBinding
import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.utils.Constants
import com.noumanch.decadeofmovies.utils.extensions.hide
import com.noumanch.decadeofmovies.utils.extensions.show


class MoviesAdapter(
    private var movies: MutableList<Movie>,
    private val movieClickCallback: (Movie, Int) -> Unit,
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
                movieClickCallback.invoke(movie, position)
            }
        }
    }

    fun getColor(position: Int): Int {
        var colorRes = 0
        when (position % 5) {
            0 -> colorRes = R.drawable.gradient_1
            1 -> colorRes = R.drawable.gradient_2
            2 -> colorRes = R.drawable.gradient_3
            3 -> colorRes = R.drawable.gradient_4
            4 -> colorRes = R.drawable.gradient_5
        }
        return colorRes
    }

    inner class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemMovieBinding.bind(view)
        fun bind(movie: Movie, position: Int) {
            binding.apply {
                movieTitleTV.text = movie.title
                movieYearTV.text = "${movie.year}"
                movieRating.rating = movie.rating.toFloat()
                movieRating.stepSize = 1.0f
                movieRating.numStars = 5
                heading.text = "${movie.year}"
                itemSelectView.setBackgroundResource(getColor(position))
                if (updatedSearchQuery.invoke().isEmpty()) {
                    heading.hide()
                } else {
                    when {
                        position == 0 -> heading.show()
                        movie.year != movies[position - 1].year -> heading.show()
                        else -> heading.hide()
                    }
                }
            }
        }
    }

    fun update(list: MutableList<Movie>) {
        //will be used to optimize recyclerview
        val diffResult = DiffUtil.calculateDiff(
            PostsDiffUtilCallback(
                movies,
                list
            )
        )
        movies = list
        diffResult.dispatchUpdatesTo(this)
    }

    fun clearData() {
        movies.clear()
        notifyDataSetChanged()
    }
}

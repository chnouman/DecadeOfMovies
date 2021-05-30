package com.noumanch.decadeofmovies.repositories

import com.noumanch.decadeofmovies.models.Movie
import io.reactivex.rxjava3.core.Single

class MovieRepositoryImpl : IMovieRepository {
    override fun getMovies(): Single<MutableList<Movie>> {
        return Single.create {
            mutableListOf(
                Movie(1, "Movie 1", "Comedy", "1991"),
                Movie(1, "Movie 1", "Comedy", "1991"),
                Movie(1, "Movie 1", "Comedy", "1991"),
                Movie(1, "Movie 1", "Comedy", "1991"),
                Movie(1, "Movie 1", "Comedy", "1991"),
                Movie(1, "Movie 1", "Comedy", "1991"),
                Movie(1, "Movie 1", "Comedy", "1991"),
                Movie(1, "Movie 1", "Comedy", "1991")
            )
        }
    }
}
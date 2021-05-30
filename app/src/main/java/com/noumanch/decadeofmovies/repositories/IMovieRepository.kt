package com.noumanch.decadeofmovies.repositories

import com.noumanch.decadeofmovies.models.Movie
import io.reactivex.rxjava3.core.Single


interface IMovieRepository {
    fun getMovies(): Single<MutableList<Movie>>
}
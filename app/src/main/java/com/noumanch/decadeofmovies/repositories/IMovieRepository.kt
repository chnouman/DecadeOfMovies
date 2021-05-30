package com.noumanch.decadeofmovies.repositories

import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.repositories.remote.responses.ImageSearchResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.Response


interface IMovieRepository {
    fun getMovies(): Single<MutableList<Movie>>
    fun getImagesFromFlickr(query: String,page:Int,perPage:Int):Single<Response<ImageSearchResponse>>
    fun getAllMoviesWithName(query: String):Single<List<Movie>>
}
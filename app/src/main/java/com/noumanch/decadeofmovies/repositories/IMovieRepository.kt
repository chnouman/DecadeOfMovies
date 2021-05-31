package com.noumanch.decadeofmovies.repositories

import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.repositories.remote.models.response.GetImagesResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


interface IMovieRepository {
    fun getImagesFromFlickr(query: String, page: Int, perPage: Int): Observable<GetImagesResponse>
    fun getAllMoviesWithName(query: String,originalData:MutableList<Movie>?): Single<List<Movie>>
    fun moviesLoadedToDb(): Boolean
    fun getAllMoviesFromDatabase(): Single<MutableList<Movie>>
    fun insertDataToDb(): Single<Boolean>
 }
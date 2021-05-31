package com.noumanch.decadeofmovies.repositories

import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.repositories.remote.models.response.GetImagesResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single


interface IMovieRepository {
    //use network to load images from flickr
    fun getImagesFromFlickr(query: String, page: Int, perPage: Int): Observable<GetImagesResponse>

    //local sorting and searching against specific query
    fun getAllMoviesWithName(query: String, originalData: MutableList<Movie>?): Single<List<Movie>>

    //check if moves are already loaded to db or not
    fun moviesLoadedToDb(): Boolean

    //retrieve all movies from db
    fun getAllMoviesFromDatabase(): Single<MutableList<Movie>>

    //insertion of movies to db taken place in this
    fun insertDataToDb(): Single<Boolean>
}
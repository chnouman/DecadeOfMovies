package com.noumanch.decadeofmovies.repositories

import com.noumanch.decadeofmovies.BuildConfig
import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.repositories.remote.FlickerApiService
import com.noumanch.decadeofmovies.repositories.remote.responses.ImageSearchResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.Response

class MovieRepositoryImpl(val flickrApi: FlickerApiService) : IMovieRepository {
    override fun getMovies(): Single<MutableList<Movie>> {
        return Single.create {
            mutableListOf(
                Movie(1, "",2028,3, arrayListOf(), arrayListOf())

            )
        }
    }
    override fun getImagesFromFlickr(query: String,page:Int,perPageImages:Int): Single<Response<ImageSearchResponse>> {
        return Single.create {
            flickrApi.getImages(
                title = query,
                apiKey = BuildConfig.FLICKER_API_KEY,
                page = page,
                perPage = perPageImages
            )
        }
    }
}

package com.noumanch.decadeofmovies.repositories.remote

import com.noumanch.decadeofmovies.repositories.remote.models.response.GetImagesResponse
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickerApiService {

    @GET("services/rest")
      fun getImages(
        @Query("method") method: String = "flickr.photos.search",
        @Query("api_key") apiKey: String,
        @Query("format") format: String = "json",
        @Query("nojsoncallback") callback: Int = 1,
        @Query("text") title: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int
    ): Observable<GetImagesResponse>

    companion object {
        const val FLICKER_API_URL = "https://api.flickr.com/"
    }
}
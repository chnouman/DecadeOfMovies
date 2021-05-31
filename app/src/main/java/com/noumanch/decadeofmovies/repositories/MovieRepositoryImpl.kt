package com.noumanch.decadeofmovies.repositories

import android.app.Application
import com.google.gson.Gson
import com.noumanch.decadeofmovies.BuildConfig
import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.repositories.local.prefs.PreferencesManager
import com.noumanch.decadeofmovies.repositories.local.db.AssetsMoviesData
import com.noumanch.decadeofmovies.repositories.local.db.MoviesDao
import com.noumanch.decadeofmovies.repositories.remote.FlickerApiService
import com.noumanch.decadeofmovies.repositories.remote.models.response.GetImagesResponse
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import java.io.IOException
import java.lang.Exception

class MovieRepositoryImpl(
    val application: Application,
    val flickrApi: FlickerApiService, val prefs: PreferencesManager,
    private val moviesDao: MoviesDao
) :
    IMovieRepository {

    override fun getAllMoviesFromDatabase(): Single<MutableList<Movie>> {
        return Single.fromCallable {
            moviesDao.getAllMovies()
        }
    }

    override fun insertDataToDb(): Single<Boolean> {
        return Single.fromCallable {
            try {
                //pick from json and store it to room db
                readAssets()?.let {
                    saveAssetMoviesInDatabase(it)
                }
            } catch (e: Exception) {
                return@fromCallable false
            }
            return@fromCallable true
        }
    }

    private fun saveAssetMoviesInDatabase(moviesStr: String) {
        val gson = Gson()
        val assetsMoviesResponse = gson.fromJson(moviesStr, AssetsMoviesData::class.java)
        assetsMoviesResponse.movies?.let { movies ->
            moviesDao.insertMovies(movies)
            // stored in db done here
            PreferencesManager.setMoviesSavedToDbStatus(true)
        }
    }

    fun readAssets(): String? {
        return try {
            val inputStream = application.assets.open("movies.json")
            val availableBytes = ByteArray(inputStream.available())
            inputStream.read(availableBytes, 0, availableBytes.size)
            String(availableBytes)
        } catch (ex: IOException) {
            ex.printStackTrace()
            null
        }
    }

    override fun getImagesFromFlickr(
        query: String,
        page: Int,
        perPageImages: Int
    ): Observable<GetImagesResponse> {
        return flickrApi.getImages(
            title = query,
            apiKey = BuildConfig.FLICKER_API_KEY,
            page = page,
            perPage = perPageImages
        )
    }

    override fun getAllMoviesWithName(
        query: String,
        originalMoviesData: MutableList<Movie>?
    ): Single<List<Movie>> {
        return Single.fromCallable {
            //do filtering on back thread
            return@fromCallable originalMoviesData?.filter {
                it.title.contains(query, true)
            }?.groupBy { it.year }
                ?.flatMap {
                    var tempList = it.value.toMutableList()
                    if (tempList.size > 5) {
                        tempList = tempList.subList(0, 5)
                    }
                    tempList
                }?.reversed()
        }
    }

    override fun moviesLoadedToDb(): Boolean =
        PreferencesManager.moviesLoaded()
}

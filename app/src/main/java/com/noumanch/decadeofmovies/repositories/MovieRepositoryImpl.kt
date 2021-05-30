package com.noumanch.decadeofmovies.repositories

import android.app.Application
import android.preference.PreferenceManager
import com.google.gson.Gson
import com.noumanch.decadeofmovies.BuildConfig
import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.repositories.local.PreferencesManager
import com.noumanch.decadeofmovies.repositories.local.db.AssetMoviesResponse
import com.noumanch.decadeofmovies.repositories.local.db.MoviesDao
import com.noumanch.decadeofmovies.repositories.remote.FlickerApiService
import com.noumanch.decadeofmovies.repositories.remote.responses.ImageSearchResponse
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import java.io.IOException
import java.io.InputStream

class MovieRepositoryImpl(
    val application: Application,
    val flickrApi: FlickerApiService, val prefs: PreferencesManager,
    private val moviesDao: MoviesDao
) :
    IMovieRepository {
    override fun getMovies(): Single<MutableList<Movie>> {
        //check if movies are already loaded to db
        return if (PreferencesManager.moviesLoaded()) {
            //load from db
            Single.fromCallable {
                getAllMoviesFromDatabase()
            }
        } else {
            //pick from json and store it to room db
            readAssets()?.let {
                saveAssetMoviesInDatabase(it)
            }
            Single.fromCallable {
                getAllMoviesFromDatabase()
            }
        }
    }

    private fun getAllMoviesFromDatabase(): MutableList<Movie> {
        return moviesDao.getAllMovies()
    }

    private fun saveAssetMoviesInDatabase(moviesStr: String) {
        val gson = Gson()
        val assetsMoviesResponse = gson.fromJson(moviesStr, AssetMoviesResponse::class.java)
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
    ): Single<Response<ImageSearchResponse>> {
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

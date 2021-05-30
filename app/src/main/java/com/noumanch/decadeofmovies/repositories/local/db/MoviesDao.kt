package com.noumanch.decadeofmovies.repositories.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noumanch.decadeofmovies.models.Movie
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow


@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(movies: List<Movie>)

    @Query("DELETE FROM ${Movie.TABLE_NAME}")
    suspend fun deleteAllMovies()

    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE movieId = :movieId")
    fun getMovieById(movieId: Int): Flow<Movie>

    @Query("SELECT * FROM ${Movie.TABLE_NAME}")
    fun getAllMovies(): MutableList<Movie>

    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE title LIKE '%' || :title || '%' ORDER BY year DESC, rating DESC")
    fun getMoviesByName(title: String): MutableList<Movie>
}
package com.noumanch.decadeofmovies.repositories.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.noumanch.decadeofmovies.models.Movie
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) class for Movies. This class is responsible for
 * communication between the app database and app repository.
 */
@Dao
interface MoviesDao {
    /**
     * Inserts [movies] into the Movies table.
     * Duplicate values are replaced in the table.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovies(movies: List<Movie>)

    /**
     * Deletes all the movies from the Movies table.
     */
    @Query("DELETE FROM ${Movie.TABLE_NAME}")
    suspend fun deleteAllMovies()

    /**
     * Fetches the movie from the Movies table whose id is [movieId].
     * @return [Flow] of [MovieModel] from database table.
     */
    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE movieId = :movieId")
    fun getMovieById(movieId: Int): Flow<Movie>

    /**
     * Fetches all the movies from the Movies table.
     * @return [Flow]
     */
    @Query("SELECT * FROM ${Movie.TABLE_NAME}")
    fun getAllMovies(): MutableList<Movie>

    /**
     * Fetches all the movies with the passed [title] ordered by year with descending rating sort
     * @return movies list
     */
    @Query("SELECT * FROM ${Movie.TABLE_NAME} WHERE title LIKE '%' || :title || '%' ORDER BY year DESC, rating DESC")
    fun getMoviesByName(title: String): List<Movie>
}
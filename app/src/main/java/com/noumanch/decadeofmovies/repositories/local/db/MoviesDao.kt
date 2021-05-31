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

    @Query("SELECT * FROM ${Movie.TABLE_NAME}")
    fun getAllMovies(): MutableList<Movie>

}
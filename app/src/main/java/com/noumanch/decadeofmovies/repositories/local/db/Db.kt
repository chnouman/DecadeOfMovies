package com.noumanch.decadeofmovies.repositories.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.utils.JsonListConverter

/**
 * The Swvl App's local SQLite database powered by Room wrapper.
 */
@Database(
    entities = [Movie::class],
    version = Db.VERSION
)
@TypeConverters(JsonListConverter::class)
 abstract class Db: RoomDatabase() {
    /**
     * Allows the app to access database through this DAO object
     * @return [MoviesDao] Data Access Object.
     */
    abstract fun getMoviesDao(): MoviesDao

    companion object {
        const val VERSION = 1
        const val DB_NAME = "decades_of_movies_db"

        @Volatile
        private var _instance: Db? = null

        fun getInstance(context: Context): Db {
            val tempInstance = _instance
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Db::class.java,
                    DB_NAME
                ).build()

                _instance = instance
                return instance
            }
        }
    }
}
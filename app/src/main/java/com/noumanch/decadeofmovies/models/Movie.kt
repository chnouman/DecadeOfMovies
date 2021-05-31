package com.noumanch.decadeofmovies.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Movie.TABLE_NAME)
data class Movie(
    @PrimaryKey(autoGenerate = true) val movieId: Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "year")
    val year: Int,
    @ColumnInfo(name = "rating")
    val rating: Int,
    @ColumnInfo(name = "cast")
    var cast: List<String>? = null,
    @ColumnInfo(name = "genres")
    var genres: List<String>? = null,

    ) : Parcelable {
    companion object {
        const val TABLE_NAME = "MovieTable"
    }
}
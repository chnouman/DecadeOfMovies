package com.noumanch.decadeofmovies.utils

import androidx.room.TypeConverter
import com.google.gson.Gson

/**
 * TypeConverter for database. This converts the List<String> to String through JSON Parsing to save
 * in the database. Used for the fields such as actors cast and generes in the MovieTable.
 */
class JsonListConverter {

    @TypeConverter
    fun fromList(list: List<String>?): String? {
        return Gson().toJson(list)
    }

    @TypeConverter
    fun toList(str: String?): List<String> {
        return Gson().fromJson(str, Array<String>::class.java).toList()
    }
}
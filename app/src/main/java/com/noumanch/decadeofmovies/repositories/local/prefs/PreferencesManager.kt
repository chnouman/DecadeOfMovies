package com.noumanch.decadeofmovies.repositories.local.prefs

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

class PreferencesManager {

    object PrefKeys {
        const val LOADED_TO_DB = "LOADED_TO_DB"
    }

    companion object {
        private var pref: SharedPreferences? = null
        fun init(context: Context) {
            val prefName = context.packageName
            pref = if (pref == null) {
                context.getSharedPreferences(prefName, Context.MODE_PRIVATE)
            } else {
                throw RuntimeException("Double init our preference")
            }
        }

        private val editor: SharedPreferences.Editor
            get() = pref!!.edit()

        fun savePref(key: String?, value: Any?) {
            val editor = editor
            when {
                value is Boolean -> {
                    editor.putBoolean(key, (value as Boolean?)!!)
                }
                value is Int -> {
                    editor.putInt(key, (value as Int?)!!)
                }
                value is Float -> {
                    editor.putFloat(key, (value as Float?)!!)
                }
                value is Long -> {
                    editor.putLong(key, (value as Long?)!!)
                }
                value is String -> {
                    editor.putString(key, value as String?)
                }
                value is Enum<*> -> {
                    editor.putString(key, value.toString())
                }
                value != null -> {
                    throw RuntimeException("Attempting to save non-primitive preference")
                }
            }
            editor.commit()
        }

        private fun <T> getPref(key: String?, defValue: T): T {
            val returnValue = pref!!.all[key] as T?
            return returnValue ?: defValue
        }

        fun moviesLoaded(): Boolean {
            return getPref(PrefKeys.LOADED_TO_DB, false)
        }

        fun remove(key: String?) {
            val editor = editor
            editor.remove(key)
            editor.commit()
        }

        fun clean() {
            editor.clear().commit()
        }

        fun setMoviesSavedToDbStatus(isSaved: Boolean) {
            savePref(PrefKeys.LOADED_TO_DB, isSaved)
        }
    }
}
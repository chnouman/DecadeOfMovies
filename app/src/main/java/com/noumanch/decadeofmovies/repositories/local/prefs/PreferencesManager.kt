package com.noumanch.decadeofmovies.repositories.local.prefs

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

class PreferencesManager {


    object PrefKeys {
        const val LOADED_TO_DB = "LOADED_TO_DB"

    }

    companion object {
        private val TAG = PreferencesManager::class.java.simpleName

        //    private static PreferencesManager instance;
        private var pref: SharedPreferences? = null

        //    public static PreferencesManager getInstance() {
        //        return instance;
        //    }
        fun init(context: Context) {
            val PREF_NAME = context.packageName
            if (pref == null) {
//            pref = android.preference.PreferenceManager.getDefaultSharedPreferences(context);

                //With this code gettting proble, as our service is on remote process
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    pref = context.getSharedPreferences(
                        PREF_NAME,
                        Context.MODE_PRIVATE or Context.MODE_MULTI_PROCESS
                    )
                } else {
                    pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                }
            } else {
                throw RuntimeException("Double init our preference")
            }
        }

        private val editor: SharedPreferences.Editor
            private get() = pref!!.edit()

        //    public static void delete(String key) {
        //        if (pref.contains(key)) {
        //            getEditor().remove(key).commit();
        //        }
        //    }
        fun savePref(key: String?, value: Any?) {
            val editor = editor
            if (value is Boolean) {
                editor.putBoolean(key, (value as Boolean?)!!)
            } else if (value is Int) {
                editor.putInt(key, (value as Int?)!!)
            } else if (value is Float) {
                editor.putFloat(key, (value as Float?)!!)
            } else if (value is Long) {
                editor.putLong(key, (value as Long?)!!)
            } else if (value is String) {
                editor.putString(key, value as String?)
            } else if (value is Enum<*>) {
                editor.putString(key, value.toString())
            } else if (value != null) {
                throw RuntimeException("Attempting to save non-primitive preference")
            }
            editor.commit()
        }

        fun <T> getPref(key: String?): T? {
            return pref!!.all[key] as T?
        }

        fun <T> getPref(key: String?, defValue: T): T {
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
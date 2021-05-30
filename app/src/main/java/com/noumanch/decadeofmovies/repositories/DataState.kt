package com.noumanch.decadeofmovies.repositories

sealed class DataState<T> {
    class Loading<T>: DataState<T>()
    data class Success<T>(val data: T): DataState<T>()
    data class Error<T>(val message: String): DataState<T>()

    companion object {
        fun <T> loading() = Loading<T>()
        fun <T> success(data: T) = Success<T>(data)
        fun <T> error(message: String) = Error<T>(message)
    }
}
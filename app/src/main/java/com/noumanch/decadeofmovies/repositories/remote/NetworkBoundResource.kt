package com.noumanch.decadeofmovies.repositories.remote

import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import com.noumanch.decadeofmovies.repositories.DataState
 import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * The NetworkBoundResource class. The idea behind this is that this is a generic
 * class to load the data from database/network in developer-friendly way. This will
 * load the data from database first. And it will fetch the data from network and
 * then save the new data in database and then gives the database data back to the UI.
 */
@ExperimentalCoroutinesApi
abstract class NetworkBoundResource<REQUEST, RESULT> {

    fun asFlow() = flow<DataState<RESULT>> {
        // Notify the loading state first
        emit(DataState.loading())

        // Load database content first
        emit(DataState.success(fetchFromLocal().first()))
    }

    /**
     * Saves retrieved from remote into the persistence storage.
     */
    @WorkerThread
    protected suspend fun saveRemoteData(response: REQUEST) { }

    /**
     * Retrieves all data from persistence storage.
     */
    @MainThread
    protected abstract fun fetchFromLocal(): Flow<RESULT>
}
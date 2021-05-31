package com.noumanch.decadeofmovies.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.repositories.IMovieRepository
import com.noumanch.decadeofmovies.ui.fragments.PostsDiffUtilCallback
import com.noumanch.decadeofmovies.utils.erros.AppError
import com.noumanch.decadeofmovies.utils.erros.Event
import com.noumanch.decadeofmovies.utils.erros.IPErrorType
import com.noumanch.decadeofmovies.utils.rxjava.ISchedulerProvider
import io.reactivex.rxjava3.disposables.Disposable
import java.net.SocketTimeoutException

class MoviesViewModel(
    private val moviesRepo: IMovieRepository,
    private val schedulerProvider: ISchedulerProvider
) : ViewModel() {

    private var getMoviesDisposable: Disposable? = null
    private var originalMoviesData: MutableList<Movie>? = null
    private val moviesListLiveData = MutableLiveData<GetMoviesViewState>()
    private val insertToDbLiveData = MutableLiveData<InsertToDbViewState>()
    fun getMoviesLiveData() = moviesListLiveData
    fun getInsertToDbLiveData() = insertToDbLiveData

    override fun onCleared() {
        super.onCleared()
        getMoviesDisposable?.dispose()
    }

    fun getMovies() {
        //call repository for data
        if (originalMoviesData != null) {
            //time to load data
            moviesListLiveData.postValue(GetMoviesViewState.Loading)
            moviesListLiveData.postValue(GetMoviesViewState.Success(originalMoviesData!!))
            return
        }
        if (moviesRepo.moviesLoadedToDb()) {
            //load from db
            getAllMoviesFromDb()
        } else {
            insertDataToDb()
        }

    }

    fun insertDataToDb() {
        moviesRepo.insertDataToDb()
            .doOnSubscribe { insertToDbLiveData.postValue(InsertToDbViewState.Loading) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({
                if (it) {
                    insertToDbLiveData.postValue(InsertToDbViewState.Success(true))
                    //data saved to db successfully
                    getAllMoviesFromDb()
                } else {
                    insertToDbLiveData.postValue(InsertToDbViewState.Success(false))
                    //fail to save data to db
                    moviesListLiveData.postValue(
                        GetMoviesViewState.Error(
                            Event(
                                AppError(
                                    "Fail to read data",
                                    "",
                                    IPErrorType.READ_ERROR
                                )
                            )
                        )
                    )
                }
            },
                { error ->
                    insertToDbLiveData.value = when (error) {
                        is SocketTimeoutException -> InsertToDbViewState.Error(
                            Event(
                                AppError(
                                    error.message ?: "",
                                    error.cause?.message ?: "",
                                    IPErrorType.NETWORK_NOT_CONNECTED
                                )
                            )
                        )
                        else -> InsertToDbViewState.Error(
                            Event(
                                AppError(
                                    error.message ?: "",
                                    error.cause?.message ?: "",
                                    IPErrorType.OTHER
                                )
                            )
                        )
                    }
                }
            )
    }

    fun getAllMoviesFromDb() {
        moviesRepo.getAllMoviesFromDatabase()
            .doOnSubscribe { moviesListLiveData.postValue(GetMoviesViewState.Loading) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({
                originalMoviesData = it
                moviesListLiveData.postValue(GetMoviesViewState.Success(it))
            },
                { error ->
                    moviesListLiveData.value = when (error) {
                        is SocketTimeoutException -> GetMoviesViewState.Error(
                            Event(
                                AppError(
                                    error.message ?: "",
                                    error.cause?.message ?: "",
                                    IPErrorType.NETWORK_NOT_CONNECTED
                                )
                            )
                        )
                        else -> GetMoviesViewState.Error(
                            Event(
                                AppError(
                                    error.message ?: "",
                                    error.cause?.message ?: "",
                                    IPErrorType.OTHER
                                )
                            )
                        )
                    }
                }
            )
    }

    fun searchMovie(query: String) {
        //do local query on original data
        moviesRepo.getAllMoviesWithName(query, originalMoviesData?: arrayListOf())
            .doOnSubscribe { moviesListLiveData.postValue(GetMoviesViewState.Loading) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({
                if (it == null || it.isEmpty()) {
                    moviesListLiveData.postValue(GetMoviesViewState.Success(arrayListOf()))
                } else {
                    moviesListLiveData.postValue(GetMoviesViewState.Success(it as ArrayList<Movie>))
                }
            },
                { error ->
                    moviesListLiveData.value = when (error) {
                        is SocketTimeoutException -> GetMoviesViewState.Error(
                            Event(
                                AppError(
                                    error.message ?: "",
                                    error.cause?.message ?: "",
                                    IPErrorType.NETWORK_NOT_CONNECTED
                                )
                            )
                        )
                        else -> GetMoviesViewState.Error(
                            Event(
                                AppError(
                                    error.message ?: "",
                                    error.cause?.message ?: "",
                                    IPErrorType.OTHER
                                )
                            )
                        )
                    }
                }
            )
    }

    sealed class GetMoviesViewState() {
        data class Success(val movies: MutableList<Movie>) : GetMoviesViewState()
        object Loading : GetMoviesViewState()
        object NoResults : GetMoviesViewState()
        data class Error(val error: Event<AppError>) : GetMoviesViewState()
    }

    sealed class InsertToDbViewState() {
        data class Success(val success: Boolean) : InsertToDbViewState()
        object Loading : InsertToDbViewState()
        data class Error(val error: Event<AppError>) : InsertToDbViewState()
    }
}
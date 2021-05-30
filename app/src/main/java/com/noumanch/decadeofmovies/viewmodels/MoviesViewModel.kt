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
    fun getMoviesLiveData() = moviesListLiveData

    override fun onCleared() {
        super.onCleared()
        getMoviesDisposable?.dispose()
    }

    fun getMovies() {
        //call repository for data
        if (originalMoviesData != null) {
            //time to load data
            moviesListLiveData.postValue(GetMoviesViewState.Success(originalMoviesData!!))
            return
        }
        moviesRepo.getMovies()
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
        moviesListLiveData.postValue(GetMoviesViewState.Loading)
        //do local query on original data
        val results = originalMoviesData?.filter {
            it.title.contains(query, true)
        }?.groupBy { it.year }
            ?.flatMap {
                var tempList = it.value.toMutableList()
                if (tempList.size > 5) {
                    tempList = tempList.subList(0, 5)
                }
                tempList
            }?.reversed()

        if (results == null || results.isEmpty()) {
            moviesListLiveData.postValue(GetMoviesViewState.Success(arrayListOf()))
        } else {
            moviesListLiveData.postValue(GetMoviesViewState.Success(results as ArrayList<Movie>))
        }
    }

    sealed class GetMoviesViewState() {
        data class Success(val movies: MutableList<Movie>) : GetMoviesViewState()
        object Loading : GetMoviesViewState()
        object NoResults : GetMoviesViewState()
        data class Error(val error: Event<AppError>) : GetMoviesViewState()
    }
}
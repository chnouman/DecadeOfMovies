package com.noumanch.decadeofmovies.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.noumanch.decadeofmovies.utils.rxjava.ISchedulerProvider
import com.noumanch.decadeofmovies.repositories.IMovieRepository
import com.noumanch.decadeofmovies.utils.erros.AppError
import com.noumanch.decadeofmovies.utils.erros.Event
import com.noumanch.decadeofmovies.utils.erros.IPErrorType
import io.reactivex.rxjava3.disposables.Disposable
import java.net.SocketTimeoutException

class MoviesViewModel(
    private val moviesRepo: IMovieRepository,
    private val schedulerProvider: ISchedulerProvider
) : ViewModel() {

    private var getMoviesDisposable: Disposable? = null
    private val moviesListLiveData = MutableLiveData<GetMoviesViewState>()

    sealed class GetMoviesViewState() {
        data class Success(val movies: MutableList<Any>) : GetMoviesViewState()
        object Loading : GetMoviesViewState()
        data class Error(val error: Event<AppError>) : GetMoviesViewState()
    }

    override fun onCleared() {
        super.onCleared()
        getMoviesDisposable?.dispose()
    }

    fun getMovies() {
        //call repository for data
        moviesRepo.getMovies()
            .map {
                //add headers if required
                return@map it as MutableList<Any>
            }
            .doOnSubscribe { moviesListLiveData.postValue(GetMoviesViewState.Loading) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({
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

    fun getMoviesLiveData() = moviesListLiveData
}
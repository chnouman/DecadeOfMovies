package com.noumanch.decadeofmovies.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noumanch.decadeofmovies.models.Image
import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.repositories.IMovieRepository
import com.noumanch.decadeofmovies.utils.erros.AppError
import com.noumanch.decadeofmovies.utils.erros.Event
import com.noumanch.decadeofmovies.utils.erros.IPErrorType
import com.noumanch.decadeofmovies.utils.rxjava.ISchedulerProvider
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException

class MovieDetailsViewModel(
    private val moviesRepo: IMovieRepository,
    private val schedulerProvider: ISchedulerProvider
) :
    ViewModel() {

    private val _currentMovieModel = MutableLiveData<Movie>()
    val currentMovieModel: LiveData<Movie> = _currentMovieModel

    private val _flickrImagesLiveData = MutableLiveData<MovieDetailsViewState>()
    val flickrImagesLiveData: LiveData<MovieDetailsViewState> = _flickrImagesLiveData


    fun init(movie: Movie) {
        _currentMovieModel.value = movie
        loadFlickerImages(movie.title)
    }

    fun loadFlickerImages(title: String) {
        moviesRepo.getImagesFromFlickr(query = title, 1, 30)
            .doOnSubscribe { _flickrImagesLiveData.postValue(MovieDetailsViewState.Loading) }
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({
                val images = it?.photos?.photos

                if (images == null) {
                    _flickrImagesLiveData.postValue(MovieDetailsViewState.NoImageFound)
                } else {
                    _flickrImagesLiveData.postValue(MovieDetailsViewState.Success(images))
                }
            },
                { error ->
                    _flickrImagesLiveData.value = when (error) {
                        is SocketTimeoutException -> MovieDetailsViewState.Error(
                            Event(
                                AppError(
                                    error.message ?: "",
                                    error.cause?.message ?: "",
                                    IPErrorType.NETWORK_NOT_CONNECTED
                                )
                            )
                        )
                        else -> MovieDetailsViewState.Error(
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

    sealed class MovieDetailsViewState {
        object Loading : MovieDetailsViewState()
        object NoImageFound : MovieDetailsViewState()
        data class Success(val images: MutableList<Image>) : MovieDetailsViewState()
        class Error(val error: Event<AppError>) : MovieDetailsViewState()
    }
}
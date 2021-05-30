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

    private val _movieModel = MutableLiveData<Movie>()
    val movieModel: LiveData<Movie> = _movieModel

    private val _uiState = MutableLiveData<MovieDetailsViewState>()
    val uiState: LiveData<MovieDetailsViewState> = _uiState


    fun init(movie: Movie) {
        _movieModel.value = movie
        loadFlickerImages(movie.title)
    }

    private fun loadFlickerImages(title: String) {
        viewModelScope.launch {
            moviesRepo.getImagesFromFlickr(query = title, 1, 30)
                .doOnSubscribe { _uiState.postValue(MovieDetailsViewState.Loading) }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe({
                     var images = it?.photos?.photos

                    if (images == null) {
                        _uiState.postValue(MovieDetailsViewState.NoImageFound)
                    } else {
                        _uiState.postValue(MovieDetailsViewState.Success(images))
                    }
                },
                    { error ->
                        _uiState.value = when (error) {
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
    }

    sealed class MovieDetailsViewState {
        object Loading : MovieDetailsViewState()
        object NoImageFound : MovieDetailsViewState()
        data class Success(val images: List<Image>) : MovieDetailsViewState()
        class Error(val message: Event<AppError>) : MovieDetailsViewState()
    }
}
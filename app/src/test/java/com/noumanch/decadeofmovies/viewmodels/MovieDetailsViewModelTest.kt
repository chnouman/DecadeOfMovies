package com.noumanch.decadeofmovies.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.noumanch.decadeofmovies.models.Image
import com.noumanch.decadeofmovies.repositories.IMovieRepository
import com.noumanch.decadeofmovies.repositories.remote.models.response.GetImagesResponse
import com.noumanch.decadeofmovies.repositories.remote.models.response.Images
import com.noumanch.decadeofmovies.rxjava.TestSchedulerProviderImpl
import io.reactivex.rxjava3.core.Observable
import junit.framework.TestCase.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MovieDetailsViewModelTest {

    private lateinit var viewModel: MovieDetailsViewModel

    @Mock
    private lateinit var moviesRepo: IMovieRepository

    @Mock
    private lateinit var observer: Observer<MovieDetailsViewModel.MovieDetailsViewState>

    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<MovieDetailsViewModel.MovieDetailsViewState>

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupObjects() {
        MockitoAnnotations.initMocks(this)
        viewModel = MovieDetailsViewModel(moviesRepo, TestSchedulerProviderImpl())
    }

    @Test
    fun verifyGetMoviesLoadingSuccessAndErrorStates() {
        //no images case
        `when`(moviesRepo.getImagesFromFlickr(anyString(), anyInt(), anyInt()))
            .thenReturn(Observable.just(GetImagesResponse()))
        viewModel.flickrImagesLiveData.observeForever(
            observer
        )
        viewModel.loadFlickerImages("")
        val errorMessage = "message"
        val errorCauseMessage = "cause"
        //error case
        `when`(moviesRepo.getImagesFromFlickr(anyString(), anyInt(), anyInt()))
            .thenReturn(Observable.error(Throwable(errorMessage, Throwable(errorCauseMessage))))
        viewModel.loadFlickerImages("")

        //success case
        `when`(moviesRepo.getImagesFromFlickr(anyString(), anyInt(), anyInt()))
            .thenReturn(
                Observable.just(
                    GetImagesResponse(
                        1,
                        Images(listOf(Image("", "", "", "", 1, "", 1, 1, 1)))
                    )
                )
            )
        viewModel.loadFlickerImages("")

        verify(observer, times(6))
            .onChanged(argumentCaptor.capture())
        val values = argumentCaptor.allValues

        //check results for No Images case
        assertEquals(
            MovieDetailsViewModel.MovieDetailsViewState.Loading,
            values[0]
        )
        assert(values[1] is MovieDetailsViewModel.MovieDetailsViewState.NoImageFound)
        assertEquals(MovieDetailsViewModel.MovieDetailsViewState.Loading, values[2])
        //check results for error case
        assertThat(
            "Second LiveData value is instance of MovieDetailsViewModel.MovieDetailsViewState.Error data class",
            values[3] is MovieDetailsViewModel.MovieDetailsViewState.Error
        )
        val errorContent =
            (values[3] as MovieDetailsViewModel.MovieDetailsViewState.Error).error.peekContent()
        assertEquals(errorMessage, errorContent.errorMessage)
        assertEquals(errorCauseMessage, errorContent.errorCause)

        //check results for success case
        assertEquals(
            MovieDetailsViewModel.MovieDetailsViewState.Loading,
            values[4]
        )
        assert(values[5] is MovieDetailsViewModel.MovieDetailsViewState.Success)

    }
}


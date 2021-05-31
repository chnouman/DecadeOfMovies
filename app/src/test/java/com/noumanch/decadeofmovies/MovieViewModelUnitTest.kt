package com.noumanch.decadeofmovies

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.noumanch.decadeofmovies.models.Movie
import com.noumanch.decadeofmovies.repositories.IMovieRepository
import com.noumanch.decadeofmovies.rxjava.TestSchedulerProviderImpl
import com.noumanch.decadeofmovies.viewmodels.MoviesViewModel
import io.reactivex.rxjava3.core.Single
import junit.framework.TestCase.assertEquals
import org.hamcrest.MatcherAssert.assertThat
import org.junit.*
import org.junit.runner.RunWith
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MovieViewModelUnitTest {

    private lateinit var viewModel: MoviesViewModel

    @Mock
    private lateinit var moviesRepo: IMovieRepository

    @Mock
    private lateinit var observer: Observer<MoviesViewModel.GetMoviesViewState>

    @Mock
    private lateinit var insertToDbLiveDataObserver: Observer<MoviesViewModel.InsertToDbViewState>

    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<MoviesViewModel.GetMoviesViewState>

    @Captor
    private lateinit var insertToDbArgumentCaptor: ArgumentCaptor<MoviesViewModel.InsertToDbViewState>

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupObjects() {
        MockitoAnnotations.initMocks(this)
        viewModel = MoviesViewModel(moviesRepo, TestSchedulerProviderImpl())
    }

    @Test
    fun verifyGetMoviesLoadingSuccessAndErrorStates() {
        `when`(moviesRepo.getAllMoviesFromDatabase())
            .thenReturn(
                Single.just(
                    mutableListOf(
                        Movie(
                            1,
                            "Movie A",
                            2001,
                            4,
                            arrayListOf(),
                            arrayListOf()
                        )
                    )
                )
            )
        viewModel.getMoviesLiveData().observeForever(
            observer
        )
        viewModel.getAllMoviesFromDb()
        val errorMessage = "message"
        val errorCauseMessage = "cause"
        `when`(moviesRepo.getAllMoviesFromDatabase())
            .thenReturn(Single.error(Throwable(errorMessage, Throwable(errorCauseMessage))))
        viewModel.getAllMoviesFromDb()

        verify(observer, times(4))
            .onChanged(argumentCaptor.capture())
        val values = argumentCaptor.allValues
        assertEquals(
            MoviesViewModel.GetMoviesViewState.Loading,
            values[0]
        )
        assert(values[1] is MoviesViewModel.GetMoviesViewState.Success)
        assertEquals(MoviesViewModel.GetMoviesViewState.Loading, values[2])
        assertThat(
            "Second LiveData value is instance of MoviesViewModel.GetMoviesViewState.Error data class",
            values[3] is MoviesViewModel.GetMoviesViewState.Error
        )
        val errorContent =
            (values[3] as MoviesViewModel.GetMoviesViewState.Error).error.peekContent()
        assertEquals(errorMessage, errorContent.errorMessage)
        assertEquals(errorCauseMessage, errorContent.errorCause)
    }

    @Test
    fun verifyDataStoredToDBLoadingSuccessAndErrorStates() {
        //true case
        `when`(moviesRepo.insertDataToDb())
            .thenReturn(
                Single.just(
                    true
                )
            )
        viewModel.getInsertToDbLiveData().observeForever(
            insertToDbLiveDataObserver
        )
        viewModel.insertDataToDb()


        //false case
        `when`(moviesRepo.insertDataToDb())
            .thenReturn(
                Single.just(
                    false
                )
            )
        viewModel.insertDataToDb()
        val errorMessage = "message"
        val errorCauseMessage = "cause"


        //error case
        `when`(moviesRepo.insertDataToDb())
            .thenReturn(Single.error(Throwable(errorMessage, Throwable(errorCauseMessage))))
        viewModel.insertDataToDb()

        verify(insertToDbLiveDataObserver, times(6))
            .onChanged(insertToDbArgumentCaptor.capture())
        val values = insertToDbArgumentCaptor.allValues
        //assertions on first case
        assertEquals(
            MoviesViewModel.InsertToDbViewState.Loading,
            values[0]
        )
        assert(values[1] is MoviesViewModel.InsertToDbViewState.Success)

        //assertions on second case
        assertEquals(
            MoviesViewModel.InsertToDbViewState.Loading,
            values[2]
        )
        assert(values[3] is MoviesViewModel.InsertToDbViewState.Success)
        val successShouldBeFalse =
            (values[3] as MoviesViewModel.InsertToDbViewState.Success).success
        assertEquals(successShouldBeFalse, false)

        //assertions on third case
        assertEquals(MoviesViewModel.InsertToDbViewState.Loading, values[4])
        assertThat(
            "Second LiveData value is instance of MoviesViewModel.InsertToDbViewState.Error data class",
            values[5] is MoviesViewModel.InsertToDbViewState.Error
        )
        val errorContent =
            (values[5] as MoviesViewModel.InsertToDbViewState.Error).error.peekContent()
        assertEquals(errorMessage, errorContent.errorMessage)
        assertEquals(errorCauseMessage, errorContent.errorCause)
    }

    @Test
    fun verifySearchMoviesLoadingSuccessAndErrorStates() {
        //success case
        `when`(moviesRepo.getAllMoviesWithName(anyString(), anyList()))
            .thenReturn(Single.just(mutableListOf(
                Movie(
                    1,
                    "Movie A",
                    2001,
                    4,
                    arrayListOf(),
                    arrayListOf()
                )
            )))

        viewModel.getMoviesLiveData().observeForever(
            observer
        )
        viewModel.searchMovie("")

        //error case
        val errorMessage = "message"
        val errorCauseMessage = "cause"
        `when`(moviesRepo.getAllMoviesWithName(anyString(), anyList()))
            .thenReturn(Single.error(Throwable(errorMessage, Throwable(errorCauseMessage))))
        viewModel.searchMovie("")

        verify(observer, times(4))
            .onChanged(argumentCaptor.capture())
        val values = argumentCaptor.allValues
        //assertions on first case
        assertEquals(
            MoviesViewModel.GetMoviesViewState.Loading,
            values[0]
        )
        assert(values[1] is MoviesViewModel.GetMoviesViewState.Success)
        //assertions on error case
        assertEquals(MoviesViewModel.GetMoviesViewState.Loading, values[2])
        assertThat(
            "Second LiveData value is instance of MoviesViewModel.GetMoviesViewState.Error data class",
            values[3] is MoviesViewModel.GetMoviesViewState.Error
        )
        val errorContent =
            (values[3] as MoviesViewModel.GetMoviesViewState.Error).error.peekContent()
        assertEquals(errorMessage, errorContent.errorMessage)
        assertEquals(errorCauseMessage, errorContent.errorCause)
    }
}


package com.noumanch.decadeofmovies.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.noumanch.decadeofmovies.R
import com.noumanch.decadeofmovies.databinding.FragmentMoviesBinding
import com.noumanch.decadeofmovies.utils.EspressoIdlingResource
import com.noumanch.decadeofmovies.utils.extensions.hide
import com.noumanch.decadeofmovies.utils.extensions.show
import com.noumanch.decadeofmovies.utils.showAlertDialog
import com.noumanch.decadeofmovies.viewmodels.MoviesViewModel
import org.koin.android.ext.android.inject

class MoviesFragment : Fragment() {

    private lateinit var adapter: MoviesAdapter

    //get from DI
    private val moviesViewModel: MoviesViewModel by inject()
    private lateinit var binding: FragmentMoviesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoviesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //observe movies data
        moviesViewModel.getMoviesLiveData().observe(this, { viewState ->
            when (viewState) {
                is MoviesViewModel.GetMoviesViewState.Error -> {
                    binding.progressBar.hide()
                    if (!viewState.error.hasBeenHandled) {
                        showAlertDialog(
                            requireContext(),
                            R.drawable.ic_baseline_error_24,
                            R.string.alert_dialog_error_title,
                            resources.getString(
                                R.string.alert_dialog_error_message_beginning,
                                viewState.error.peekContent().errorMessage,
                                viewState.error.peekContent().errorCause
                            ),
                            null
                        )
                    }
                }
                MoviesViewModel.GetMoviesViewState.Loading -> binding.progressBar.show()
                is MoviesViewModel.GetMoviesViewState.Success -> {
                    binding.progressBar.hide()
                    EspressoIdlingResource.decrement()
                    if (viewState.movies.size > 0) {
                        binding.apply {
                            recyclerView.show()
                            emptyLayout.root.hide()
                            emptyLayout.emptyTitleTV.hide()
                            emptyLayout.emptySubHeadingTV.hide()
                            //fill up some colors
                            adapter.update(viewState.movies)
                            adapter.notifyDataSetChanged()
                            recyclerView.scrollToPosition(0)
                        }
                    } else {
                        populateEmptyView()
                    }
                }
                else -> {
                }
            }
        })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

    }

    private fun setupViews() {
        binding.apply {
            topLayout.setBackgroundResource(R.drawable.cardview_bg)
            //create Adapter
            adapter = MoviesAdapter(arrayListOf(), { movie, position ->
                //navigate to next movie detail screen
                findNavController().navigate(
                    MoviesFragmentDirections.actionMoviesFragmentToMovieDetailsFragment(
                        movie, position
                    )
                )
            }, {
                txtSearch.text.toString()
            })
            recyclerView.adapter = adapter
            progressBar.show()
            // Search Text
            txtSearch.addTextChangedListener { text ->
                if (text.isNullOrEmpty()) {
                    moviesViewModel.getMovies()
                } else {
                    moviesViewModel.searchMovie(text.toString().trim())
                }
            }
        }
        //load data
        EspressoIdlingResource.increment()
        moviesViewModel.getMovies()
    }

    private fun populateEmptyView() {
        adapter.clearData()
        binding.apply {
            recyclerView.hide()
            emptyLayout.root.show()
            emptyLayout.emptyTitleTV.show()
            emptyLayout.emptySubHeadingTV.show()
        }
    }
}
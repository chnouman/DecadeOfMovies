package com.noumanch.decadeofmovies.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.noumanch.decadeofmovies.R
import com.noumanch.decadeofmovies.databinding.FragmentMoviesBinding
import com.noumanch.decadeofmovies.utils.extensions.hide
import com.noumanch.decadeofmovies.utils.extensions.show
import com.noumanch.decadeofmovies.utils.showAlertDialog
import com.noumanch.decadeofmovies.viewmodels.MoviesViewModel
import org.koin.android.ext.android.inject

class MoviesFragment : Fragment() {

    private lateinit var adapter: MoviesAdapter
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
                            R.drawable.ic_empty_invoice,
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
                    if (viewState.movies.size > 0) {
                        binding.recyclerView.show()
                        binding.emptyLayout.root.hide()
                        binding.emptyLayout.emptyTitleTV.hide()
                        binding.emptyLayout.emptySubHeadingTV.hide()
                        binding.emptyLayout.emptyIV.hide()
                        adapter.update(viewState.movies)
                        adapter.notifyDataSetChanged()
                    } else {
                        populateEmptyView()
                    }
                }
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()

    }

    private fun setupViews() {
        //create Adapter
        adapter = MoviesAdapter(mutableListOf()) { movie ->
        }
        binding.recyclerView.adapter = adapter
        binding.progressBar.show()
        //load data
        moviesViewModel.getMovies()
    }


    private fun populateEmptyView() {
        adapter.clearData()
        binding.recyclerView.hide()
        binding.emptyLayout.root.show()
        binding.emptyLayout.emptyTitleTV.show()
        binding.emptyLayout.emptySubHeadingTV.show()
        binding.emptyLayout.emptyIV.show()
    }
}
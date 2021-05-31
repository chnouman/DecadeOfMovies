package com.noumanch.decadeofmovies.ui.fragments.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.noumanch.decadeofmovies.R
import com.noumanch.decadeofmovies.databinding.FragmentMovieDetailBinding
import com.noumanch.decadeofmovies.models.Image
import com.noumanch.decadeofmovies.utils.extensions.hide
import com.noumanch.decadeofmovies.utils.extensions.show
import com.noumanch.decadeofmovies.viewmodels.MovieDetailsViewModel
import org.koin.android.ext.android.inject


class MovieDetailsFragment : Fragment() {

    private val viewModel by inject<MovieDetailsViewModel>()
    private lateinit var binding: FragmentMovieDetailBinding
    private val photosList = arrayListOf<Image>()
    private lateinit var recyclerAdapter: MoviesDetailAdapter
    private val args by navArgs<MovieDetailsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMovieDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViews()
        initObservations()
        viewModel.init(args.movie)
    }

    fun setupViews() {
        binding.backButton.setOnClickListener { findNavController().navigateUp() }
        context?.let {
            recyclerAdapter = MoviesDetailAdapter(photosList) { selectedImage ->
                //any action
            }
            binding.recyclerPhotos.isNestedScrollingEnabled = false
            binding.recyclerPhotos.adapter = recyclerAdapter
        }
    }

    private fun initObservations() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MovieDetailsViewModel.MovieDetailsViewState.Success -> {
                    photosList.addAll(state.images)
                    binding.recyclerPhotos.show()
                    recyclerAdapter.notifyDataSetChanged()
                }
                is MovieDetailsViewModel.MovieDetailsViewState.Error -> {
                    //show error if required

                }
                is MovieDetailsViewModel.MovieDetailsViewState.Loading -> {
                    //show progress bar
                }
                is MovieDetailsViewModel.MovieDetailsViewState.NoImageFound -> {
                    //show no images found view
                }
            }
        }

        viewModel.movieModel.observe(viewLifecycleOwner) { movie ->
            binding.apply {
                movieNameTV.text = movie.title
                txtRating.rating = movie.rating.toFloat()
                movieYearTV.text = "${movie.year}"
                setupGenresChips(movie.genres)
                setupActors(movie.cast)
            }
        }

    }

    private fun setupGenresChips(genres: List<String>?) {
        binding.apply {
            if (genres == null) {
                chipGroupGenres.hide()

            } else {
                genres.forEach {
                    val chip = Chip(context)
                    chip.text = it
                    chip.setBackgroundResource(R.drawable.borders_black)
                    chipGroupGenres.addView(chip)
                }
            }
        }
    }

    fun setupActors(actors: List<String>?) {
        binding.apply {
            if (actors == null) {
                lblActors.hide()
                txtActors.hide()
            } else {
                actors.forEach {
                    txtActors.append(it + ", ")
                }
            }
        }
    }
}
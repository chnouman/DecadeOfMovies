package com.noumanch.decadeofmovies.ui.fragments.detail

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
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
        viewModel.flickrImagesLiveData.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MovieDetailsViewModel.MovieDetailsViewState.Success -> {
                    if (state.images.isNotEmpty()) {
                        binding.emptyLayout.root.hide()
                        binding.progressBar.hide()
                        photosList.addAll(state.images)
                        binding.recyclerPhotos.show()
                        recyclerAdapter.notifyDataSetChanged()
                    } else {
                        populateEmptyView()
                    }
                }
                is MovieDetailsViewModel.MovieDetailsViewState.Error -> {
                    //show error if required
                    populateEmptyView()
                }
                is MovieDetailsViewModel.MovieDetailsViewState.Loading -> {
                    //show progress bar
                    binding.progressBar.show()
                }
                is MovieDetailsViewModel.MovieDetailsViewState.NoImageFound -> {
                    //show no images found view
                    populateEmptyView()
                }
            }
        }

        viewModel.currentMovieModel.observe(viewLifecycleOwner) { movie ->
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
                    chip.chipCornerRadius = 16.0f
                    chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                    chip.setChipBackgroundColorResource(getColor(args.position))
                    chipGroupGenres.addView(chip)
                }
            }
        }
    }

    private fun getColor(position: Int): Int {
        var colorRes = 0
        when (position % 5) {
            0 -> colorRes = R.color.purple_700
            1 -> colorRes = R.color.red_700
            2 -> colorRes = R.color.pink_700
            3 -> colorRes = R.color.blue_700
            4 -> colorRes = R.color.voilet_700
        }
        return colorRes
    }

    fun setupActors(actors: List<String>?) {
        binding.apply {
            if (actors == null) {
                lblActors.hide()
                txtActors.hide()
            } else {
                actors.forEach {
                    txtActors.append("$it, ")
                }
            }
        }
    }

    private fun populateEmptyView() {
        binding.progressBar.hide()
        binding.recyclerPhotos.hide()
        binding.emptyLayout.root.show()
        binding.emptyLayout.emptyTitleTV.show()
        binding.emptyLayout.emptySubHeadingTV.show()
        binding.emptyLayout.emptyTitleTV.text = getString(R.string.no_images_found)
        binding.emptyLayout.emptySubHeadingTV.text = getString(R.string.images_not_found_desc)
    }
}
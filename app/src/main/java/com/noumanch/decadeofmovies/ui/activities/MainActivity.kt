package com.noumanch.decadeofmovies.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.noumanch.decadeofmovies.R
import com.noumanch.decadeofmovies.viewmodels.MoviesViewModel
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val moviesViewModel: MoviesViewModel by inject<MoviesViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }
}
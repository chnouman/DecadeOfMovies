package com.noumanch.decadeofmovies

import android.app.Application
import com.noumanch.decadeofmovies.di.repositoryModule
import com.noumanch.decadeofmovies.di.viewModelModule
import org.koin.core.context.startKoin

class DecadesOfMoviesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            // use modules
            modules(viewModelModule, repositoryModule)
        }
    }
}
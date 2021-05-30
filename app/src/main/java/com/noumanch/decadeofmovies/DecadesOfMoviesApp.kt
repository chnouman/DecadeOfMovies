package com.noumanch.decadeofmovies

import android.app.Application
import com.noumanch.decadeofmovies.di.repositoryModule
import com.noumanch.decadeofmovies.di.viewModelModule
import com.noumanch.decadeofmovies.repositories.local.PreferencesManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class DecadesOfMoviesApp : Application() {

    override fun onCreate() {
        super.onCreate()
        PreferencesManager.init(this)
        startKoin {
            // use modules
            androidContext(this@DecadesOfMoviesApp)
            modules(viewModelModule, repositoryModule)
        }
    }
}
package com.noumanch.decadeofmovies.di

import com.noumanch.decadeofmovies.utils.rxjava.AppSchedulerProviderImpl
import com.noumanch.decadeofmovies.utils.rxjava.ISchedulerProvider
import com.noumanch.decadeofmovies.repositories.IMovieRepository
import com.noumanch.decadeofmovies.repositories.MovieRepositoryImpl
import com.noumanch.decadeofmovies.viewmodels.MoviesViewModel

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MoviesViewModel(get(), get()) }
}

val repositoryModule = module {

    single<ISchedulerProvider> { AppSchedulerProviderImpl() }

    single<IMovieRepository> { MovieRepositoryImpl() }


}
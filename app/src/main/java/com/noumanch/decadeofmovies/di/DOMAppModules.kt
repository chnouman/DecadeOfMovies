package com.noumanch.decadeofmovies.di

import com.noumanch.decadeofmovies.utils.rxjava.AppSchedulerProviderImpl
import com.noumanch.decadeofmovies.utils.rxjava.ISchedulerProvider
import com.noumanch.decadeofmovies.repositories.IMovieRepository
import com.noumanch.decadeofmovies.repositories.MovieRepositoryImpl
import com.noumanch.decadeofmovies.repositories.local.PreferencesManager
import com.noumanch.decadeofmovies.repositories.local.db.Db
import com.noumanch.decadeofmovies.repositories.local.db.MoviesDao
import com.noumanch.decadeofmovies.repositories.remote.FlickerApiService
import com.noumanch.decadeofmovies.ui.fragments.detail.MovieDetailsViewModel
import com.noumanch.decadeofmovies.viewmodels.MoviesViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val viewModelModule = module {

    viewModel { MoviesViewModel(get(), get()) }

    viewModel { MovieDetailsViewModel(get(), get()) }
}

val repositoryModule = module {

    single<ISchedulerProvider> { AppSchedulerProviderImpl() }

    single<IMovieRepository> { MovieRepositoryImpl(get(), get(), get(), get()) }

    single<PreferencesManager> { PreferencesManager() }
    single<MoviesDao> { Db.getInstance(androidApplication()).getMoviesDao() }

    single<FlickerApiService> {
        val retrofit = Retrofit.Builder()
            .baseUrl(FlickerApiService.FLICKER_API_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        retrofit.create(FlickerApiService::class.java)

    }

    single {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

    }
}
package com.noumanch.decadeofmovies.utils.rxjava

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class AppSchedulerProviderImpl : ISchedulerProvider {

    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }

}
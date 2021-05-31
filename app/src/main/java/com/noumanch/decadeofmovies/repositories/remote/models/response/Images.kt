package com.noumanch.decadeofmovies.repositories.remote.models.response

import com.noumanch.decadeofmovies.models.Image

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
 data class Images(
    @SerializedName("photo")
    @Expose
    var photos: MutableList<Image>? = null
)
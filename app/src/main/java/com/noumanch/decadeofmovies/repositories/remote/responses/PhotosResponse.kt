package com.noumanch.decadeofmovies.repositories.remote.responses

import com.noumanch.decadeofmovies.models.Image


data class PhotosResponse(
    var photos: List<Image>? = null
)
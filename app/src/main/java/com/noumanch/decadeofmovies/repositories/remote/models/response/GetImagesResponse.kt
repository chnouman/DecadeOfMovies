package com.noumanch.decadeofmovies.repositories.remote.models.response


import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class GetImagesResponse (

    @SerializedName("page")
    @Expose
    var page: Int? = null,

    @SerializedName("photos")
    @Expose
    var photos: Images? = null
)
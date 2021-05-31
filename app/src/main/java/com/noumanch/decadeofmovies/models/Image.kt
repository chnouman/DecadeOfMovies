package com.noumanch.decadeofmovies.models


data class Image(
    var id: String? = null,
    var secret: String? = null,
    var server: String? = null,
    var farm: Int? = null
) {
    fun getImageUrl(): String {
        return "https://farm$farm.static.flickr.com/$server/${id}_$secret.jpg"
    }
}
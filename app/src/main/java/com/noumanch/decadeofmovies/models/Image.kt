package com.noumanch.decadeofmovies.models


data class Image(


    var id: String? = null,
    var owner: String? = null,
    var secret: String? = null,
    var server: String? = null,
    var farm: Int? = null,
    var title: String? = null,
    var ispublic: Int? = null,
    var isfriend: Int? = null,
    var isfamily: Int? = null
) {
    fun getImageUrl(): String {
        return "https://farm$farm.static.flickr.com/$server/${id}_$secret.jpg"
    }
}
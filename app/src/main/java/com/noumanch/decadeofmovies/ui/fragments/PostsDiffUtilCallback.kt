package com.noumanch.decadeofmovies.ui.fragments

import androidx.recyclerview.widget.DiffUtil
import com.noumanch.decadeofmovies.models.Movie


class PostsDiffUtilCallback(private val oldList: MutableList<Movie>, private val newList: MutableList<Movie>) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].movieId == newList[newItemPosition].movieId

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = true
}
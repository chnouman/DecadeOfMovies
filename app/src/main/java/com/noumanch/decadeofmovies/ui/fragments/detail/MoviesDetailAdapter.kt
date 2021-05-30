package com.noumanch.decadeofmovies.ui.fragments.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.noumanch.decadeofmovies.R
import com.noumanch.decadeofmovies.databinding.ItemImageBinding
import com.noumanch.decadeofmovies.databinding.ItemMovieBinding
import com.noumanch.decadeofmovies.databinding.ItemMovieHeadingBinding
import com.noumanch.decadeofmovies.models.Image
import com.noumanch.decadeofmovies.models.Movie

class MoviesDetailAdapter(
    private var images: MutableList<Image>,
    private val imageClickCallback: (Image) -> Unit,
) :
    RecyclerView.Adapter<MoviesDetailAdapter.ImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int) =
        ImageViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        )

    override fun getItemCount() = images.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        images[position].let { image ->
            holder.bind(image)
            holder.itemView.setOnClickListener { imageClickCallback.invoke(image) }
        }
    }


    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemImageBinding.bind(view)
        fun bind(image: Image) {
            Glide.with(binding.imageView.context).load(image.getImageUrl())
                .into(binding.imageView)

        }
    }

    fun update(list: List<Image>) {
        images.clear()
        images.addAll(list)
        notifyDataSetChanged()
    }

    fun clearData() {
        images.clear()
        notifyDataSetChanged()
    }
}

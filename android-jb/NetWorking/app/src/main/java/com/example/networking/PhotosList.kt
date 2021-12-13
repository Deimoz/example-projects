package com.example.networking

import android.graphics.Bitmap
import android.os.AsyncTask
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.item.view.*

@Parcelize
data class Photo(val photo: Bitmap, val fullPhotoURL: String, val description: String?) : Parcelable

class PhotoAdapter(var photos: List<Photo>, val onClick: (Photo) -> Unit) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PhotoViewHolder {
        val holder = PhotoViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item, parent, false)
        )
        holder.root.setOnClickListener {
            onClick(photos[holder.adapterPosition])
        }
        return holder

    }

    override fun getItemCount() = photos.size

    override fun onBindViewHolder(
        holder: PhotoViewHolder,
        position: Int
    ) = holder.bind(photos[position])

    inner class PhotoViewHolder(val root: View) : RecyclerView.ViewHolder(root) {

        fun bind(photo: Photo) {
            with(root) {
                image.setImageBitmap(photo.photo)
                description.text = photo.description
            }
        }
    }
}
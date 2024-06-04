package com.tasnimulhasan.common.extfun

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso

fun Picasso.loadImage(drawable: Int, url: String, imageView: ImageView) {
    val imageUrl = url.ifEmpty { "no_image" }
    this.load(imageUrl).placeholder(drawable).into(imageView)
}

fun ImageView.loadImage(placeholder: Int, url: String) {
    val imageUrl = url.ifEmpty { "no_image" }
    Picasso.get().load(imageUrl).placeholder(drawable)
        .into(this)
}

fun ImageView.loadImageUrl(drawable: Int, url: String) {
    val imageUrl = url.ifEmpty { "no_image" }

    Picasso.get().load(imageUrl).placeholder(drawable)
        .into(this)
}

fun ImageView.loadGifImage(drawable: Int, context: Context) {
    Glide.with(context).load(drawable).into(this)
}
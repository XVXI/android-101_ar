package com.transcode.smartcity101p2.extension

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.transcode.smartcity101p2.R

fun ImageView.load(url: String?, resourceError: Int): Boolean {
    val checkImage = checkImage(url, resourceError)
    if (checkImage) {
        context?.let {
            val requestOptions = RequestOptions
                    .placeholderOf(resourceError)
                    .error(resourceError)
            Glide.with(it).setDefaultRequestOptions(requestOptions).load(url).into(this)
        }
    }
    return checkImage
}

fun ImageView.loadround(url: String?, resourceError: Int? = null): Boolean {
    val checkImage = checkImage(url, resourceError)
    if (checkImage) {
        context?.let {
            Glide.with(it).load(url).apply(RequestOptions().transform(RoundedCorners(it.resources.getDimensionPixelSize(R.dimen.dimen_round_corner)))).into(this)
        }
    }
    return checkImage
}

fun ImageView.loadCircle(url: String?, resourceError: Int? = null): Boolean {
    val checkImage = checkImage(url, resourceError)
    if (checkImage) {
        context?.let {
            Glide.with(it).load(url).apply(RequestOptions().transform(CircleCrop())).into(this)
        }
    }
    return checkImage
}

fun ImageView.loadCircle(bitmap: Bitmap) {
    context?.let {
        Glide.with(it).load(bitmap).apply(RequestOptions().transform(CircleCrop())).into(this)
    }
}

fun ImageView.loadround(bitmap: Bitmap) {
    context?.let {
        Glide.with(it).load(bitmap).apply(RequestOptions().transform(RoundedCorners(it.resources.getDimensionPixelSize(R.dimen.dimen_round_corner)))).into(this)
    }
}

fun ImageView.loadroundCrop(bitmap: Bitmap) {
    context?.let {
        Glide.with(it).load(bitmap).apply(RequestOptions().transform(MultiTransformation(CenterCrop(), RoundedCorners(it.resources.getDimensionPixelSize(R.dimen.dimen_round_corner))))).into(this)
    }
}

private fun ImageView.checkImage(url: String?, resourceError: Int? = null): Boolean {
    return when {
        url.isNullOrEmpty() -> {
            if (resourceError != null)
                setImageResource(resourceError)
            else
                setImageDrawable(null)
            false
        }
        else -> {
            true
        }
    }
}
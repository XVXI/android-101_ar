package com.transcode.smartcity101p2

import android.os.Bundle
import com.bumptech.glide.Glide
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_gif.*
import kotlinx.android.synthetic.main.appbar_main2.view.*

class GifImageActivity : CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gif)

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("")
        appBar.leftBt.setOnClickListener { finish() }

//        gif.load("", R.raw.nyan_cat);
        Glide.with(this).asGif().load(R.raw.horwote_panorama).into(gif)

    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
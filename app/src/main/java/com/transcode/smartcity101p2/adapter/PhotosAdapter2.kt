package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import kotlinx.android.synthetic.main.item_image_banner2.view.*

class PhotosAdapter2(var context: Context) : PagerAdapter() {
    var list = arrayListOf<String>()
    private var listener: ClickItem? = null
    private var scaleType: ImageView.ScaleType? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` == view
    }

    override fun getCount(): Int = list.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_image_banner2, container, false)
        view.layoutParams = lp

        scaleType?.let {
            view.image.scaleType = scaleType
        }

        view.image.load(list[position], R.mipmap.placeholder_image)

        view.image.setOnClickListener {
            listener?.onClickItem()
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun setData(list: ArrayList<String>) {
        this.list = list
    }

    fun setClickItemListener(listener: ClickItem) {
        this.listener = listener
    }

    fun setImageScaleType(scaleType: ImageView.ScaleType) {
        this.scaleType = scaleType
    }

    interface ClickItem {
        fun onClickItem()
    }
}
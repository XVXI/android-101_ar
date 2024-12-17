package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v4.view.PagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.ConvertDate
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.NewsResponse
import kotlinx.android.synthetic.main.item_news_pagger.view.*

class NewsPaggerAdapter(var context: Context) : PagerAdapter() {
    var list = arrayListOf<NewsResponse>()
    private var listener: ClickItem? = null
    private var scaleType: ImageView.ScaleType? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return `object` == view
    }

    override fun getCount(): Int = list.size

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_news_pagger, container, false)
        view.layoutParams = lp

        val data = list[position]

        val images = data.imgs ?: arrayListOf()

        view.image_news.load(if (images.size > 0) {
            images[0]
        } else {
            ""
        }, R.mipmap.placeholder_image)
        view.text_title.text = data.NewsTitle
        view.text_desc.text = data.NewsDetail
        view.text_gene.text = data.newsCatName
        view.text_date.ConvertDate(data.CreateDateTime ?: "0")

        view.image_news.setOnClickListener {
            listener?.onClickItem(data)
        }

        view.setOnClickListener {
            listener?.onClickItem(data)
        }

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    fun setData(list: ArrayList<NewsResponse>) {
        this.list = list
    }

    fun setClickItemListener(listener: ClickItem) {
        this.listener = listener
    }

    fun setImageScaleType(scaleType: ImageView.ScaleType) {
        this.scaleType = scaleType
    }

    interface ClickItem {
        fun onClickItem(data: NewsResponse)
    }
}
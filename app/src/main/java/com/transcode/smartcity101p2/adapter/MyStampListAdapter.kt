package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import kotlinx.android.synthetic.main.item_stamp.view.*
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.MyStampResponse
import com.transcode.smartcity101p2.utils.AppUtils

class MyStampListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<MyStampResponse.MyStampResponseData>()

    private var listener: MyStampClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_stamp, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.text_stamp_title.text = data.place_name

        val imageUrl = if (data.stamp_img.isNullOrEmpty()) {
            ""
        } else {
            AppUtils.getImageUrl(data.stamp_img.toString())
        }
        holder.itemView.image_stamp.load(imageUrl, R.drawable.circle_white_purple)
        holder.itemView.setOnClickListener {
            listener?.onMyStampClickItem(data)
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(rv.context, 3)
        rv.adapter = this
    }

    fun setData(list: ArrayList<MyStampResponse.MyStampResponseData>) {
        this.list = list
    }

    fun setClickItemListener(listener: MyStampClickItem) {
        this.listener = listener
    }

    interface MyStampClickItem {
        fun onMyStampClickItem(res: MyStampResponse.MyStampResponseData)
    }
}
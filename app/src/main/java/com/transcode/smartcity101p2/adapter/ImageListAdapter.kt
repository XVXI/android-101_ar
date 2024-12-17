package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.item_image_selected.view.*
import android.view.View
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.utils.AppUtils

class ImageListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<String>()

    private var listener: ClickDeleteItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_image_selected, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val url = AppUtils.getImageUrl(data)
        holder.itemView.image_selected.load(url, R.mipmap.icon_app)
        holder.itemView.delete.visibility = View.GONE
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(rv.context, 3)
        rv.adapter = this
    }

    fun setData(list: ArrayList<String>) {
        this.list = list
    }

    fun setDeleteClickListener(listener: ClickDeleteItem) {
        this.listener = listener
    }

    interface ClickDeleteItem {
        fun onClickDeleteItem(position: Int)
    }
}
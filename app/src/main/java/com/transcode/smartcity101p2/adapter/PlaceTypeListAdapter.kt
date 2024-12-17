package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import kotlinx.android.synthetic.main.item_place_type_selected.view.*
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.PlaceTypeResponse
import com.transcode.smartcity101p2.utils.AppUtils

class PlaceTypeListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<PlaceTypeResponse.PlaceTypeResponseData>()

    private var listener: ClickTypeItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_place_type_selected, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val url = if (data.type_name.isNullOrEmpty()) {
            ""
        } else {
            AppUtils.getImageUrl(data.icon_url)
        }
        holder.itemView.image_selected.load(url, R.mipmap.icon_all_inactive)
        holder.itemView.setOnClickListener {
            listener?.onClickTypeItem(data, url)
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(rv.context, 4) as RecyclerView.LayoutManager?
        rv.adapter = this
    }

    fun setData(list: ArrayList<PlaceTypeResponse.PlaceTypeResponseData>) {
        this.list = list
    }

    fun setClickTypeListener(listener: ClickTypeItem) {
        this.listener = listener
    }

    interface ClickTypeItem {
        fun onClickTypeItem(data: PlaceTypeResponse.PlaceTypeResponseData, url: String)
    }
}
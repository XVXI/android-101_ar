package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import kotlinx.android.synthetic.main.item_fav_place.view.*
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.travel.response.FavPlaceListResponse

class TravelFavPlaceAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<FavPlaceListResponse.FavPlaceListResponseData>()

    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_fav_place, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.item_plan_text_remark.text = data.place_name

        var imageUrl = ""
        if (data.img.size > 0) {
            val img = data.img[0]
            imageUrl = img.image_path ?: ""
        }

        holder.itemView.item_plan_image.load(imageUrl, R.mipmap.icon_travel_header)

        holder.itemView.setOnClickListener {
            listener?.onClickItem(data.place_name ?: "", data.place_id ?: "0")
        }

        holder.itemView.item_place_fav.setOnClickListener {
            listener?.onClickRemoveFavItem(data.place_id ?: "0")
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(rv.context, 2)
        rv.adapter = this
    }

    fun setData(list: ArrayList<FavPlaceListResponse.FavPlaceListResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onClickItem(title: String, place_id: String)
        fun onClickRemoveFavItem(place_id: String)
    }
}
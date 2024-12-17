package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.model.PlaceListByTypeResponse
import com.transcode.smartcity101p2.model.PlaceSuggestionResponse
import kotlinx.android.synthetic.main.item_suggestion.view.*
import kotlinx.android.synthetic.main.item_type_place.view.*

class AllSearchAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<HashMap<String, Any>>()
    var listener: AllSearchAdapterClickItem? = null

    companion object {
        val TYPE_SUGGESTION = 0
        val TYPE_NORMAL = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_SUGGESTION -> {
                val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.item_suggestion, parent, false)
                val viewHolder = ViewHolder(view)
                view.layoutParams = lp
                viewHolder
            }
            else -> {
                val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.item_type_place, parent, false)
                val viewHolder = ViewHolder(view)
                view.layoutParams = lp
                viewHolder
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val data = list[position]

        when (getItemViewType(position)) {
            TYPE_SUGGESTION -> {
                val adapter = SuggestPlaceAdapter(context)
                adapter.setRecyclerView(holder.itemView.recycler_suggest)
                adapter.setData(data["data"] as ArrayList<PlaceSuggestionResponse.PlaceSuggestionResponseData>)
                adapter.setClickListener(listener)
            }
            else -> {
                holder.itemView.text_type_place_title.text = data["type_name"] as String
                val adapter = TypePlaceAdapter(context)
                adapter.setRecyclerView(holder.itemView.recycler_type_place)
                adapter.setData(data["data"] as ArrayList<PlaceListByTypeResponse.PlaceMaker>)
                adapter.setClickListener(listener)
            }
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<HashMap<String, Any>>) {
        this.list = list
    }

    fun setClickListener(listener: AllSearchAdapterClickItem) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]["type"] as String) {
            "0" -> {
                TYPE_SUGGESTION
            }
            else -> TYPE_NORMAL
        }
    }

    interface AllSearchAdapterClickItem {
        fun onAllSearchAdapterClickItem(place_name: String, place_id: String)
    }
}
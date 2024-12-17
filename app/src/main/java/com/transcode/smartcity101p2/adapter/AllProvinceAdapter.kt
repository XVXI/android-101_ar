package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.model.ProvinceResponse
import kotlinx.android.synthetic.main.item_city.view.*

class AllProvinceAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<ProvinceResponse.ProvinceResponseData>()
    private var listener: AllProvinceAdapterClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_province, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.title.text = list[position].province_name
        holder.itemView.setOnClickListener {
            listener?.onAllProvinceAdapterClickItem(list[position])
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<ProvinceResponse.ProvinceResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: AllProvinceAdapterClickItem) {
        this.listener = listener
    }

    interface AllProvinceAdapterClickItem {
        fun onAllProvinceAdapterClickItem(res: ProvinceResponse.ProvinceResponseData)
    }
}
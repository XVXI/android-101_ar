package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.model.QueueSlotResponse
import kotlinx.android.synthetic.main.item_select_time.view.*

class TimeListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<QueueSlotResponse.QueueSlotData>()
    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_select_time, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]

        var stime = data.QueueFromTime ?: "00:00:00"
        stime = stime.substring(0, stime.length - 3)
        if (stime.startsWith("0")) stime = stime.substring(1, stime.length)

        var etime = data.QueueToTime ?: "00:00:00"
        etime = etime.substring(0, etime.length - 3)
        if (etime.startsWith("0")) etime = etime.substring(1, etime.length)

        holder.itemView.title.text = stime + " - " + etime
        val limit = Integer.parseInt(data.QueueLimit ?: "0")
        val checkIn = Integer.parseInt(data.CountCheckin ?: "0")
        if (checkIn >= limit) {
            holder.itemView.layoutFrame.setBackgroundResource(R.drawable.rounded_corner_textbox_bg_gray_tran)
            holder.itemView.setOnClickListener({})
        } else {
            holder.itemView.setOnClickListener({
                for (i in list) {
                    i.selected = false
                    if (i.QueueSlotId == data.QueueSlotId) {
                        i.selected = true
                    }
                }
                notifyDataSetChanged()
                listener?.onclickItem(data)
            })
        }

        if (data.selected) {
            holder.itemView.layoutFrame.setBackgroundResource(R.drawable.rounded_corner_textbox_bg_blue_tran)
            holder.itemView.mark.visibility = View.VISIBLE
        } else {
            holder.itemView.layoutFrame.setBackgroundResource(R.drawable.rounded_corner_textbox_bg_gray_tran)
            holder.itemView.mark.visibility = View.INVISIBLE
        }

    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<QueueSlotResponse.QueueSlotData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: QueueSlotResponse.QueueSlotData)
    }
}
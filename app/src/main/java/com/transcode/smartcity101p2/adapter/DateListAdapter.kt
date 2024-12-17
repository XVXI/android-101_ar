package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.item_select_date.view.*

class DateListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<HashMap<String, Any>>()
    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_select_date, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.title.text = AppUtils.getDateString("EEE " + AppUtils.formateDate3, data["date"] as Long)
        holder.itemView.setOnClickListener {
            for (i in list) {
                i["selected"] = i["date"] == data["date"]
            }
            notifyDataSetChanged()
            listener?.onclickItem(data)
        }

        if (data["selected"] as Boolean) {
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

    fun setData(list: ArrayList<HashMap<String, Any>>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: HashMap<String, Any>)
    }
}
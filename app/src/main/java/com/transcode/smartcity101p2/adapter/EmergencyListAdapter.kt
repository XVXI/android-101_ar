package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.emergency.EmergencyListResponse
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.item_emergency_list.view.*

class EmergencyListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<EmergencyListResponse.EmergencyListResponseData>()
    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_emergency_list, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.title.text = data.emer_detail
        holder.itemView.text_status.text = data.emer_status_name

        val emer_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.EMER_CHAT_DB)
                ?: arrayListOf()
        var count = 0

        for (d in emer_db) {
            if (data.emer_id == d["emer_id"]) {
                count += (d["count"] ?: "0").toInt()
            }
        }
        holder.itemView.text_message_size.text = count.toString()

        if (count > 0) {
            holder.itemView.text_message_size.visibility = View.VISIBLE
        } else {
            holder.itemView.text_message_size.visibility = View.GONE
        }

        when (data.emer_status_id) {
            "1" -> {
                holder.itemView.text_status.setTextColor(context.resources.getColor(R.color.orange))
                holder.itemView.image_status.load("", R.mipmap.status_start)
            }
            "2" -> {
                holder.itemView.text_status.setTextColor(context.resources.getColor(R.color.red2))
                holder.itemView.image_status.load("", R.mipmap.status_progress)
            }
            "3" -> {
                holder.itemView.text_status.setTextColor(context.resources.getColor(R.color.green2))
                holder.itemView.image_status.load("", R.mipmap.status_done)
            }
        }

        val date = if (!data.update_datetime.isNullOrEmpty()) {
            data.update_datetime ?: ""
        } else {
            data.create_datetime ?: ""
        }

        val dateMill = AppUtils.dateStringToMillis(date, arrayOf(AppUtils.formateDate0))
        holder.itemView.text_update.text = AppUtils.getDateString(AppUtils.formateDate3, dateMill)

        when (data.emer_type_id) {
            "1" -> {
                holder.itemView.image_type.load("", R.mipmap.icona_077)
            }
            "2" -> {
                holder.itemView.image_type.load("", R.mipmap.icona_088)
            }
            "3" -> {
                holder.itemView.image_type.load("", R.mipmap.icona_099)
            }
            "4" -> {
                holder.itemView.image_type.load("", R.mipmap.icona_1010)
            }
            "5" -> {
                holder.itemView.image_type.load("", R.mipmap.icona_1111)
            }
            "6" -> {
                holder.itemView.image_type.load("", R.mipmap.icona_1212)
            }
        }

        holder.itemView.setOnClickListener {
            listener?.onclickItem(data)
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<EmergencyListResponse.EmergencyListResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: EmergencyListResponse.EmergencyListResponseData)
    }
}
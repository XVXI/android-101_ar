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
import com.transcode.smartcity101p2.model.complain.ComplainListResponse
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.item_complain_list.view.*

class ComplainListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<ComplainListResponse.ComplainListResponseData>()
    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_complain_list, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.title.text = data.complain_detail
        holder.itemView.text_status.text = data.complain_status_name

        val complain_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.COMPLAIN_CHAT_DB)
                ?: arrayListOf()
        var count = 0

        for (d in complain_db) {
            if (data.complain_id == d["complain_id"]) {
                count += (d["count"] ?: "0").toInt()
            }
        }
        holder.itemView.text_message_size.text = count.toString()

        if (count > 0) {
            holder.itemView.text_message_size.visibility = View.VISIBLE
        } else {
            holder.itemView.text_message_size.visibility = View.GONE
        }

        when (data.complain_status_id) {
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

        when (data.complain_type_id) {
            "1" -> {
                holder.itemView.image_type.load("", R.mipmap.icon_complain_1)
            }
            "2" -> {
                holder.itemView.image_type.load("", R.mipmap.icon_complain_2)
            }
            "3" -> {
                holder.itemView.image_type.load("", R.mipmap.icon_complain_3)
            }
            "4" -> {
                holder.itemView.image_type.load("", R.mipmap.icon_complain_4)
            }
            "5" -> {
                holder.itemView.image_type.load("", R.mipmap.icon_complain_5)
            }
            "6" -> {
                holder.itemView.image_type.load("", R.mipmap.icon_complain_6)
            }
            "7" -> {
                holder.itemView.image_type.load("", R.mipmap.icon_complain_7)
            }
            "8" -> {
                holder.itemView.image_type.load("", R.mipmap.icon_complain_8)
            }
            "9" -> {
                holder.itemView.image_type.load("", R.mipmap.icon_complain_9)
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

    fun setData(list: ArrayList<ComplainListResponse.ComplainListResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: ComplainListResponse.ComplainListResponseData)
    }
}
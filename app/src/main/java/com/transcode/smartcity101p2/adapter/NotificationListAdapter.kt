package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.item_noti_emergency_list.view.*
import kotlinx.android.synthetic.main.item_notification_header.view.*
import kotlinx.android.synthetic.main.item_notification_queue_list.view.*

class NotificationListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<Any>()
    private var listener: ClickItem? = null

    companion object {
        val TYPE_HEADER = 0
        val TYPE_QUEUE = 1
        val TYPE_EMER = 2
        val TYPE_COMPLAIN = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            TYPE_HEADER -> {
                val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.item_notification_header, parent, false)
                val viewHolder = ViewHolder(view)
                view.layoutParams = lp
                viewHolder
            }
            TYPE_EMER -> {
                val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.item_noti_emergency_list, parent, false)
                val viewHolder = ViewHolder(view)
                view.layoutParams = lp
                viewHolder
            }
            TYPE_COMPLAIN -> {
                val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.item_noti_emergency_list, parent, false)
                val viewHolder = ViewHolder(view)
                view.layoutParams = lp
                viewHolder
            }
            TYPE_QUEUE -> {
                val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.item_notification_queue_list, parent, false)
                val viewHolder = ViewHolder(view)
                view.layoutParams = lp
                viewHolder
            }
            else -> {
                val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.item_notification_header, parent, false)
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
        when (getItemViewType(position)) {
            TYPE_HEADER -> {
                holder.itemView.notification_title.text = list[position] as String
            }
            TYPE_EMER -> {
                val data = list[position] as HashMap<String, String>
                holder.itemView.item_noti_emer_title_text.text = data["title"]

                when (data["status"]) {
                    "0" -> {
                        holder.itemView.item_noti_emer_status_text0.text = "ได้รับเรื่องแล้วค่ะ"
                        holder.itemView.item_noti_emer_status_text.text = "รับเรื่อง"
                        holder.itemView.item_noti_emer_status_text.setTextColor(context.resources.getColor(R.color.orange))
                        holder.itemView.item_noti_emer_status_image.load("", R.mipmap.status_start)
                    }
                    "1" -> {
                        holder.itemView.item_noti_emer_status_text0.text = "เจ้าหน้าที่กำลังดำเนินการค่ะ"
                        holder.itemView.item_noti_emer_status_text.text = "กำลังดำเนินการ"
                        holder.itemView.item_noti_emer_status_text.setTextColor(context.resources.getColor(R.color.red2))
                        holder.itemView.item_noti_emer_status_image.load("", R.mipmap.status_progress)
                    }
                    "2" -> {
                        holder.itemView.item_noti_emer_status_text0.text = "ดำเนินการเรียบร้อยแล้วค่ะ"
                        holder.itemView.item_noti_emer_status_text.text = "สำเร็จ"
                        holder.itemView.item_noti_emer_status_text.setTextColor(context.resources.getColor(R.color.green2))
                        holder.itemView.item_noti_emer_status_image.load("", R.mipmap.status_done)
                    }
                }

                holder.itemView.setOnClickListener {
                    listener?.onclickItem(list[position])
                }
            }
            TYPE_COMPLAIN -> {
                val data = list[position] as HashMap<String, String>
                holder.itemView.item_noti_emer_title_text.text = data["title"]

                when (data["status"]) {
                    "0" -> {
                        holder.itemView.item_noti_emer_status_text0.text = "ได้รับเรื่องแล้วค่ะ"
                        holder.itemView.item_noti_emer_status_text.text = "รับเรื่อง"
                        holder.itemView.item_noti_emer_status_text.setTextColor(context.resources.getColor(R.color.orange))
                        holder.itemView.item_noti_emer_status_image.load("", R.mipmap.status_start)
                    }
                    "1" -> {
                        holder.itemView.item_noti_emer_status_text0.text = "เจ้าหน้าที่กำลังดำเนินการค่ะ"
                        holder.itemView.item_noti_emer_status_text.text = "กำลังดำเนินการ"
                        holder.itemView.item_noti_emer_status_text.setTextColor(context.resources.getColor(R.color.red2))
                        holder.itemView.item_noti_emer_status_image.load("", R.mipmap.status_progress)
                    }
                    "2" -> {
                        holder.itemView.item_noti_emer_status_text0.text = "ดำเนินการเรียบร้อยแล้วค่ะ"
                        holder.itemView.item_noti_emer_status_text.text = "สำเร็จ"
                        holder.itemView.item_noti_emer_status_text.setTextColor(context.resources.getColor(R.color.green2))
                        holder.itemView.item_noti_emer_status_image.load("", R.mipmap.status_done)
                    }
                }

                holder.itemView.setOnClickListener {
                    listener?.onclickItem(list[position])
                }
            }
            TYPE_QUEUE -> {
                val data = list[position] as MyQueueResponse
                holder.itemView.item_noti_queue_title.text = data.QueueName
                holder.itemView.item_noti_queue_number.text = data.QueueNo
                when {
                    data.QueueType == "1" -> {
                        val dateBook = "วันที่กดคิว " + "วันนี้"
                        val before = "รออีก " + data.BeforeQueue + " คน"
                        holder.itemView.item_noti_queue_date.text = "วันนี้"
                        holder.itemView.item_noti_queue_time.text = before
                    }
                    data.QueueType == "2" -> {
                        val chooseDatetime = data.ChooseDatetime ?: "0"
                        val timeMills = AppUtils.dateStringToMillis(chooseDatetime, arrayOf(AppUtils.formateDate2))
                        holder.itemView.item_noti_queue_date.text = AppUtils.getDateString("EEEE " + AppUtils.formateDate3, timeMills)
                        var fromtime = data.QueueFromTime ?: "12:00"
                        if (fromtime.length > 3) fromtime = fromtime.substring(0, fromtime.length - 3)
                        var totime = data.QueueToTime ?: "12:00"
                        if (totime.length > 3) totime = totime.substring(0, totime.length - 3)
                        val times = "$fromtime - $totime"
                        holder.itemView.item_noti_queue_time.text = times
                    }
                    else -> {
                        val chooseDatetime = data.ChooseDatetime ?: "0"
                        val timeMills = AppUtils.dateStringToMillis(chooseDatetime, arrayOf(AppUtils.formateDate2))
                        holder.itemView.item_noti_queue_date.text = AppUtils.getDateString("EEEE " + AppUtils.formateDate3, timeMills)
                        var fromtime = data.QueueFromTime ?: "12:00"
                        if (fromtime.length > 3) fromtime = fromtime.substring(0, fromtime.length - 3)
                        var totime = data.QueueToTime ?: "12:00"
                        if (totime.length > 3) totime = totime.substring(0, totime.length - 3)
                        val times = "$fromtime - $totime"
                        holder.itemView.item_noti_queue_time.text = times
                    }
                }

                holder.itemView.setOnClickListener {
                    listener?.onclickItem(list[position])
                }
            }
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<Any>) {
        this.list = list
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is String -> {
                TYPE_HEADER
            }
            is MyQueueResponse -> {
                TYPE_QUEUE
            }
            else -> {
                val data = list[position] as HashMap<String, String>
                if (data["type"] == "complain") {
                    TYPE_COMPLAIN
                } else {
                    TYPE_EMER
                }
            }
        }
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: Any)
    }
}
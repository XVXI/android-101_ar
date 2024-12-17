package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.extension.loadround
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.model.QueueListResponse
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.item_myqueue_list.view.*
import kotlinx.android.synthetic.main.item_queue_list.view.*

class AllQueueListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<Any>()
    private var listener: ClickItem? = null

    val VIEW_TYPE_HEADER = 0
    val VIEW_TYPE_MYQUEUE = 1
    val VIEW_TYPE_NORMAL_QUEUE = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = when (viewType) {
            VIEW_TYPE_NORMAL_QUEUE -> inflater.inflate(R.layout.item_queue_list, parent, false)
            VIEW_TYPE_MYQUEUE -> inflater.inflate(R.layout.item_myqueue_list, parent, false)
            VIEW_TYPE_HEADER -> inflater.inflate(R.layout.item_queue_header, parent, false)
            else -> inflater.inflate(R.layout.item_queue_header, parent, false)
        }
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        when (getItemViewType(position)) {
            VIEW_TYPE_NORMAL_QUEUE -> {
                data as QueueListResponse
                holder.itemView.title_queue.text = data.QueueName
                holder.itemView.setOnClickListener {
                    listener?.onclickItem(list[position])
                }

                holder.itemView.icon_queue.load(data.CoverImgUrl, R.mipmap.book)

                holder.itemView.icon_queue_round.loadround(data.CoverImgUrl, R.mipmap.book)

            }
            VIEW_TYPE_MYQUEUE -> {
                data as MyQueueResponse
                holder.itemView.title.text = data.QueueName
                holder.itemView.queue_number.text = data.QueueNo
                holder.itemView.type.text = convertHeader(data.QueueType ?: "")
                when (data.QueueType) {
                    "1" -> {
                        holder.itemView.next_queue.visibility = View.VISIBLE
                        holder.itemView.queue_details.visibility = View.GONE
                        val texts = "รออีก " + data.BeforeQueue + " คน"
                        holder.itemView.next_queue.text = texts
                    }
                    "2" -> {
                        holder.itemView.next_queue.visibility = View.GONE
                        holder.itemView.queue_details.visibility = View.VISIBLE

                        val dateMills = AppUtils.dateStringToMillis(data.ChooseDatetime
                                ?: "", arrayOf(AppUtils.formateDate2))

                        val date = "รับบริการ " + AppUtils.getDateString(AppUtils.formateDate3, dateMills)
                        val time = "เวลา " + AppUtils.getDateString(AppUtils.formateDate4, dateMills) + " น."

                        holder.itemView.date.text = date
                        holder.itemView.time.text = time
                    }
                    "3" -> {
                        holder.itemView.next_queue.visibility = View.GONE
                        holder.itemView.queue_details.visibility = View.VISIBLE

                        val dateMills = AppUtils.dateStringToMillis(data.ChooseDatetime
                                ?: "", arrayOf(AppUtils.formateDate2))

                        val date = "รับบริการ " + AppUtils.getDateString(AppUtils.formateDate3, dateMills)
                        val time = "เวลา " + AppUtils.getDateString(AppUtils.formateDate4, dateMills) + " น."

                        holder.itemView.date.text = date
                        holder.itemView.time.text = time
                    }
                    else -> {
                        holder.itemView.next_queue.visibility = View.VISIBLE
                        holder.itemView.queue_details.visibility = View.GONE
                    }
                }
                holder.itemView.setOnClickListener {
                    listener?.onclickItem(list[position])
                }
                holder.itemView.layout_scroll.setOnClickListener {
                    listener?.onclickItem(list[position])
                }
            }
            VIEW_TYPE_HEADER -> {
                data as String
                holder.itemView.title.text = data
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

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: Any)
    }

    override fun getItemViewType(position: Int): Int {
        when (list[position]) {
            is String -> return VIEW_TYPE_HEADER
            is MyQueueResponse -> return VIEW_TYPE_MYQUEUE
            is QueueListResponse -> return VIEW_TYPE_NORMAL_QUEUE
            else -> return VIEW_TYPE_NORMAL_QUEUE
        }
    }

    private fun convertHeader(type: String): String {
        return when (type) {
            "1" -> "จองทันที"
            "2" -> "จองล่วงหน้า"
            "3" -> "จองนอกสถานที่"
            else -> "คิวของคุณ"
        }
    }
}
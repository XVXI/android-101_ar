package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.PlanDetailResponse
import kotlinx.android.synthetic.main.item_plan_header.view.*
import kotlinx.android.synthetic.main.item_travel_plan_list.view.*
import java.util.*
import kotlin.collections.ArrayList


class TravelPlanListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<Any>()
    private var listener: ClickItem? = null
    val VIEW_TYPE_DAY = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_DAY) {
            val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_plan_header, parent, false)
            val viewHolder = ViewHolder(view)
            view.layoutParams = lp
            viewHolder
        } else {
            val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_travel_plan_list, parent, false)
            val viewHolder = ViewHolder(view)
            view.layoutParams = lp
            viewHolder
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            list[position] is String -> VIEW_TYPE_DAY
            else -> super.getItemViewType(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_DAY) {
            holder.itemView.text_day.text = "Day " + list[position]
        } else {
            val data = list[position] as PlanDetailResponse.DataDetails

            holder.itemView.up_dash.visibility = View.VISIBLE
            holder.itemView.down_dash.visibility = View.VISIBLE
            holder.itemView.text_distance.visibility = View.GONE

            if (data.distance.isNullOrEmpty()) {
                holder.itemView.text_distance.visibility = View.GONE
            } else {
                holder.itemView.text_distance.visibility = View.VISIBLE
                holder.itemView.text_distance.text = data.distance + " KM"
            }

            if (position >= itemCount - 1) {
                holder.itemView.text_distance.visibility = View.GONE
            }

            holder.itemView.up_dash.removeAllViews()
            holder.itemView.down_dash.removeAllViews()

            for (i in 0..50) {
                val tv1 = TextView(context)
                val tv2 = TextView(context)
                if (i % 2 == 0) {
                    tv1.setBackgroundColor(context.resources.getColor(R.color.purple))
                    tv2.setBackgroundColor(context.resources.getColor(R.color.purple))
                } else {
                    tv1.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
                    tv2.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
                }

                tv1.layoutParams = LinearLayout.LayoutParams(10, 10)
                tv2.layoutParams = LinearLayout.LayoutParams(10, 10)

                holder.itemView.up_dash.addView(tv1)
                holder.itemView.down_dash.addView(tv2)
            }

//            if (position == 0) {
//                holder.itemView.up_dash.visibility = View.INVISIBLE
//                if (list.size <= 1) {
//                    holder.itemView.down_dash.visibility = View.INVISIBLE
//                }
//            } else if (position == list.size - 1) {
//                holder.itemView.down_dash.visibility = View.INVISIBLE
//            }
            val prePos = if (position > 0) {
                position - 1
            } else {
                0
            }
            val pastPos = if (position < list.size - 1) {
                position + 1
            } else {
                list.size - 1
            }

            if (position > prePos) {
                if (list[prePos] is String) {
                    holder.itemView.up_dash.visibility = View.INVISIBLE
                }
            }

            if (position < pastPos) {
                if (list[pastPos] is String) {
                    holder.itemView.down_dash.visibility = View.INVISIBLE
                    holder.itemView.text_distance.visibility = View.GONE
                }
            }

            if (position == list.size - 1) {
                holder.itemView.down_dash.visibility = View.INVISIBLE
            }

            var pos = 0
            for (d in list) {
                if (d is PlanDetailResponse.DataDetails) {
                    pos += 1

                    if (d.PlaceId == data.PlaceId) {
                        break
                    }
                }
            }

            holder.itemView.text_plan_id.text = pos.toString()

            var imageUrl = ""
            if (data.img.size > 0) {
                val img = data.img[0]
                imageUrl = img.image_path ?: ""
            }

            holder.itemView.image_image.load(imageUrl, R.mipmap.icon_travel_header)

            holder.itemView.text_title.text = data.PlaceName
            holder.itemView.text_desc.text = data.Remark

            try {

                val calendar = Calendar.getInstance()
                val day = calendar.get(Calendar.DAY_OF_WEEK)

                var currentDayType = when (day) {
                    Calendar.SUNDAY -> {
                        "1"
                    }
                    Calendar.MONDAY -> {
                        "2"
                    }
                    Calendar.TUESDAY -> {
                        "3"
                    }
                    Calendar.WEDNESDAY -> {
                        "4"
                    }
                    Calendar.THURSDAY -> {
                        "5"
                    }
                    Calendar.FRIDAY -> {
                        "6"
                    }
                    Calendar.SATURDAY -> {
                        "7"
                    }
                    else -> {
                        "1"
                    }
                }

                holder.itemView.text_open.text = "Open : -"
                holder.itemView.text_close.text = "Close : -"

                var store = PlanDetailResponse.StoreOpens()
                store.StoreopenId = "0"

                for (d in data.StoreOpens) {
                    if (d.Daytype == currentDayType) {
                        try {
                            if ((d.StoreopenId ?: "0").toInt() > (store.StoreopenId
                                            ?: "0").toInt()) {
                                store = d
                            }
                        } catch (e: Exception) {
                        }
                    }
                }

                store.OpenTime?.let {
                    val open = if (it.length >= 7) {
                        it.substring(0, it.length - 3)
                    } else {
                        it
                    }
                    holder.itemView.text_open.text = "Open : $open"
                }
                store.CloseTime?.let {
                    val close = if (it.length >= 7) {
                        it.substring(0, it.length - 3)
                    } else {
                        it
                    }
                    holder.itemView.text_close.text = "Close : $close"
                }
            } catch (ignore: Exception) {
            }

            holder.itemView.item_location.setOnClickListener {
                val gmmIntentUri = Uri.parse("geo:" + data.Lat + "," + data.Lng + "?q=" + data.Lat + "," + data.Lng + "(" + data.PlaceName + ")")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                if (mapIntent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(mapIntent)
                }
            }

            if (!data.ContractTel.isNullOrEmpty()) {
                holder.itemView.item_tel.setOnClickListener {
                    val phone = data.ContractTel
                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                    context.startActivity(intent)
                }
            } else {
                holder.itemView.item_tel.setOnClickListener { }
            }

            holder.itemView.setOnClickListener {
                listener?.onclickItem(data)
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
        fun onclickItem(res: PlanDetailResponse.DataDetails)
    }
}
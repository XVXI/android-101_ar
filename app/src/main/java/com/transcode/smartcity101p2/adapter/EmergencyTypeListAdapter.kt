package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.emergency.EmergencyTypeResponse
import kotlinx.android.synthetic.main.item_grid.view.*

class EmergencyTypeListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<EmergencyTypeResponse.EmergencyTypeResponseData>()
    var currentType = EmergencyTypeResponse.EmergencyTypeResponseData()
    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_grid, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setCurrentDataType(type: EmergencyTypeResponse.EmergencyTypeResponseData) {
        currentType = type
        notifyDataSetChanged()
    }

    fun getCurrentDataType(): EmergencyTypeResponse.EmergencyTypeResponseData = currentType

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.title.text = when(data.emer_type_id){
            "1" -> context.getString(R.string.emergency_emergency_type_1)
            "2" -> context.getString(R.string.emergency_emergency_type_2)
            "3" -> context.getString(R.string.emergency_emergency_type_3)
            "4" -> context.getString(R.string.emergency_emergency_type_4)
            "5" -> context.getString(R.string.emergency_emergency_type_5)
            "6" -> context.getString(R.string.emergency_emergency_type_6)
            else -> ""
        }
        if (currentType.emer_type_id == data.emer_type_id) {
//            holder.itemView.layoutFrame.setBackgroundResource(R.drawable.rounded_corner_button_bg_blue)
            when (data.emer_type_id) {
                "1" -> {
                    holder.itemView.image.load("", R.mipmap.icona_01)
                }
                "2" -> {
                    holder.itemView.image.load("", R.mipmap.icona_02)
                }
                "3" -> {
                    holder.itemView.image.load("", R.mipmap.icona_03)
                }
                "4" -> {
                    holder.itemView.image.load("", R.mipmap.icona_04)
                }
                "5" -> {
                    holder.itemView.image.load("", R.mipmap.icona_05)
                }
                "6" -> {
                    holder.itemView.image.load("", R.mipmap.icona_06)
                }
            }
        } else {
//            holder.itemView.layoutFrame.setBackgroundResource(R.drawable.rounded_corner_textbox_bg)
            when (data.emer_type_id) {
                "1" -> {
                    holder.itemView.image.load("", R.mipmap.icona_07)
                }
                "2" -> {
                    holder.itemView.image.load("", R.mipmap.icona_08)
                }
                "3" -> {
                    holder.itemView.image.load("", R.mipmap.icona_09)
                }
                "4" -> {
                    holder.itemView.image.load("", R.mipmap.icona_10)
                }
                "5" -> {
                    holder.itemView.image.load("", R.mipmap.icona_11)
                }
                "6" -> {
                    holder.itemView.image.load("", R.mipmap.icona_12)
                }
            }
        }

        holder.itemView.setOnClickListener {
            if (currentType.emer_type_id == data.emer_type_id) {
                listener?.onclickItem(EmergencyTypeResponse.EmergencyTypeResponseData())
            } else {
                listener?.onclickItem(data)
            }
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<EmergencyTypeResponse.EmergencyTypeResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: EmergencyTypeResponse.EmergencyTypeResponseData)
    }
}
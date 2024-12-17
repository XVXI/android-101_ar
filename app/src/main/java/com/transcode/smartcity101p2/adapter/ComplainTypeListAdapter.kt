package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.complain.ComplainTypeResponse
import kotlinx.android.synthetic.main.item_grid.view.*

class ComplainTypeListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<ComplainTypeResponse.ComplainTypeResponseData>()
    var currentType = ComplainTypeResponse.ComplainTypeResponseData()
    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_grid, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setCurrentDataType(type: ComplainTypeResponse.ComplainTypeResponseData) {
        currentType = type
        notifyDataSetChanged()
    }

    fun getCurrentDataType(): ComplainTypeResponse.ComplainTypeResponseData = currentType

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.title.text = when(data.complain_type_id){
            "1" -> context.getString(R.string.complain_complain_type_1)
            "2" -> context.getString(R.string.complain_complain_type_2)
            "3" -> context.getString(R.string.complain_complain_type_3)
            "4" -> context.getString(R.string.complain_complain_type_4)
            "5" -> context.getString(R.string.complain_complain_type_5)
            "6" -> context.getString(R.string.complain_complain_type_6)
            "7" -> context.getString(R.string.complain_complain_type_7)
            "8" -> context.getString(R.string.complain_complain_type_8)
            "9" -> context.getString(R.string.complain_complain_type_9)
            else -> ""
        }
        holder.itemView.title.isSelected = true
        if (currentType.complain_type_id == data.complain_type_id) {
            when (data.complain_type_id) {
                "1" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_1_active)
                }
                "2" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_2_active)
                }
                "3" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_3_active)
                }
                "4" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_4_active)
                }
                "5" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_5_active)
                }
                "6" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_6_active)
                }
                "7" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_7_active)
                }
                "8" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_8_active)
                }
                "9" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_9_active)
                }
            }
        } else {
            when (data.complain_type_id) {
                "1" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_1_inactive)
                }
                "2" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_2_inactive)
                }
                "3" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_3_inactive)
                }
                "4" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_4_inactive)
                }
                "5" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_5_inactive)
                }
                "6" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_6_inactive)
                }
                "7" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_7_inactive)
                }
                "8" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_8_inactive)
                }
                "9" -> {
                    holder.itemView.image.load("", R.mipmap.icon_complain_9_inactive)
                }
            }
        }

        holder.itemView.setOnClickListener {
            if (currentType.complain_type_id == data.complain_type_id) {
                listener?.onclickItem(ComplainTypeResponse.ComplainTypeResponseData())
            } else {
                listener?.onclickItem(data)
            }
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(rv.context, 4)
        rv.adapter = this
    }

    fun setData(list: ArrayList<ComplainTypeResponse.ComplainTypeResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: ComplainTypeResponse.ComplainTypeResponseData)
    }
}
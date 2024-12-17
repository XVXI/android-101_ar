package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.PlanListResponse
import com.transcode.smartcity101p2.model.emergency.EmergencyTypeResponse
import kotlinx.android.synthetic.main.item_plan.view.*

class PlanListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<PlanListResponse.PlanListResponseData>()
    var currentType = EmergencyTypeResponse.EmergencyTypeResponseData()
    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(CoreApplication.getScreenWidth() - context.resources.getDimensionPixelSize(R.dimen.dp30), LinearLayout.LayoutParams.MATCH_PARENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_plan, parent, false)
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
        holder.itemView.item_plan_text_title.text = data.PlanName
        holder.itemView.item_plan_text_remark.text = data.Remark

        holder.itemView.item_plan_image.load(data.img ?: "", R.mipmap.icon_travel_header)

        holder.itemView.setOnClickListener {
            listener?.onclickItem(data)
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<PlanListResponse.PlanListResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: PlanListResponse.PlanListResponseData)
    }
}
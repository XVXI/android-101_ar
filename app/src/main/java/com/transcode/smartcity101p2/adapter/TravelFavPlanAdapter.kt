package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import kotlinx.android.synthetic.main.item_fav_plan.view.*
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.PlanListResponse
import com.transcode.smartcity101p2.model.travel.response.FavPlanListResponse

class TravelFavPlanAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<FavPlanListResponse.FavPlanListResponseData>()

    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_fav_plan, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.item_plan_text_title.text = data.plan_name
        holder.itemView.item_plan_text_remark.text = data.remark

        holder.itemView.item_plan_image.load(data.img ?: "", R.mipmap.icon_travel_header)

        holder.itemView.setOnClickListener {
            var clickData = PlanListResponse.PlanListResponseData()
            clickData.PlanId = data.plan_id
            clickData.CreateDatetime = data.create_datetime
            clickData.UpdateDatetime = data.update_datetime
            clickData.Day = data.day
            clickData.PlanName = data.plan_name
            clickData.img = data.img
            clickData.StatusId = data.status_id
            clickData.Remark = data.remark

            listener?.onClickItem(clickData)
        }

        holder.itemView.item_plan_fav.setOnClickListener {
            listener?.onClickRemoveFavItem(data.plan_id ?: "0")
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(rv.context, 2)
        rv.adapter = this
    }

    fun setData(list: ArrayList<FavPlanListResponse.FavPlanListResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onClickItem(res: PlanListResponse.PlanListResponseData)
        fun onClickRemoveFavItem(plain_id: String)
    }
}
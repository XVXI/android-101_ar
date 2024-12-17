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
import com.transcode.smartcity101p2.model.CityFunctionResponse
import kotlinx.android.synthetic.main.item_city_function.view.*

class AccountMenuAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<CityFunctionResponse>()
    var listener: ClickItem? = null

    companion object {
        val TYPE_EDIT = 0
        val TYPE_LOGOUT = 1
        val TYPE_LINE = 2
        val TYPE_NORMAL = 3
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == TYPE_LINE) {
            val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
            val view = View(context)
            val viewHolder = ViewHolder(view)
            view.layoutParams = lp
            view.setBackgroundColor(context.resources.getColor(R.color.gray))
            viewHolder
        } else {
            val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_city_function, parent, false)
            val viewHolder = ViewHolder(view)
            view.layoutParams = lp
            viewHolder
        }
    }

    override fun getItemCount(): Int {
        return list.size + 2
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_LOGOUT -> {
                holder.itemView.title.text = context.getString(R.string.logout_text)
                holder.itemView.icon.load("" , R.mipmap.logout)
                holder.itemView.setOnClickListener {
                    listener?.onclickItem(CityFunctionResponse(), TYPE_LOGOUT)
                }
            }
//            TYPE_EDIT -> {
//                holder.itemView.title.text = context.getString(R.string.account_text)
//                holder.itemView.icon.load("" , R.mipmap.user_edit)
//                holder.itemView.setOnClickListener {
//                    listener?.onclickItem(CityFunctionResponse(), TYPE_EDIT)
//                }
//            }
            TYPE_LINE -> {
                holder.itemView.setOnClickListener {
                    listener?.onclickItem(CityFunctionResponse(), TYPE_LINE)
                }
            }
            else -> {
                holder.itemView.title.text = list[position].FunctionName

                holder.itemView.icon.load(list[position].MbIconUrl , R.mipmap.placeholder_image)

                holder.itemView.setOnClickListener {
                    listener?.onclickItem(list[position], TYPE_NORMAL)
                }
            }
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<CityFunctionResponse>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            itemCount - 1 -> {
                TYPE_LOGOUT
            }
//            itemCount - 2 -> {
//                TYPE_EDIT
//            }
            itemCount - 2 -> {
                TYPE_LINE
            }
            else -> TYPE_NORMAL
        }
    }

    interface ClickItem {
        fun onclickItem(res: CityFunctionResponse, type: Int)
    }
}
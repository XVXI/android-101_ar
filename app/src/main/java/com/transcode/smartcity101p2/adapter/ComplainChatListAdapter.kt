package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.model.complain.ComplainByIDResponse
import kotlinx.android.synthetic.main.item_chat_my.view.*
import kotlinx.android.synthetic.main.item_chat_officer.view.*

class ComplainChatListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<ComplainByIDResponse.ComplainByIDResponseDialog>()
    private var listener: ClickItem? = null

    val VIEW_TYPE_MY = 1
    val View_TYPE_OFFICER = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return if (viewType == VIEW_TYPE_MY) {
            val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_chat_my, parent, false)
            val viewHolder = ViewHolder(view)
            view.layoutParams = lp
            viewHolder
        } else {
            val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.item_chat_officer, parent, false)
            val viewHolder = ViewHolder(view)
            view.layoutParams = lp
            viewHolder
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        if (getItemViewType(position) == VIEW_TYPE_MY) {
            holder.itemView.my_chat_text.text = data.dialog_msg
        } else {
            holder.itemView.office_chat_text.text = data.dialog_msg
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<ComplainByIDResponse.ComplainByIDResponseDialog>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: ComplainByIDResponse.ComplainByIDResponseDialog)
    }

    override fun getItemViewType(position: Int): Int {
        return if (list[position].citizen != null) {
            VIEW_TYPE_MY
        } else {
            View_TYPE_OFFICER
        }
    }
}
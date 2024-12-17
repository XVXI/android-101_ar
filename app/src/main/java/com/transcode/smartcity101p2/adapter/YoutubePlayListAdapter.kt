package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.YoutubePlayListResponse
import kotlinx.android.synthetic.main.item_youtubeplaylist.view.*

class YoutubePlayListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<YoutubePlayListResponse.PlayListItem>()
    private var listener: ClickItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_youtubeplaylist, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val snippet = data.snippet
        holder.itemView.title.text = snippet?.title

        snippet?.thumbnails?.default?.let {
            holder.itemView.thumb.load(it.url.toString(), R.mipmap.icon_app)
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

    fun setData(list: ArrayList<YoutubePlayListResponse.PlayListItem>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    interface ClickItem {
        fun onclickItem(res: YoutubePlayListResponse.PlayListItem)
    }
}
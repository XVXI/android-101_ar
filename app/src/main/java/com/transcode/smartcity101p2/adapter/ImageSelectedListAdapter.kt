package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_image_selected.view.*
import android.graphics.BitmapFactory
import android.support.v7.widget.LinearLayoutManager
import android.util.Base64
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.loadroundCrop


class ImageSelectedListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<String>()

    private var listener: ClickDeleteItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(context.resources.getDimensionPixelSize(R.dimen.dp90), context.resources.getDimensionPixelSize(R.dimen.dp80))
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_image_selected, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        val decodedString = Base64.decode(data, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
//        holder.itemView.image_selected.setImageBitmap(bitmap)
        holder.itemView.image_selected.loadroundCrop(bitmap)
        holder.itemView.delete.setOnClickListener {
            listener?.onClickDeleteItem(position)
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<String>) {
        this.list = list
    }

    fun setDeleteClickListener(listener: ClickDeleteItem) {
        this.listener = listener
    }

    interface ClickDeleteItem {
        fun onClickDeleteItem(position: Int)
    }
}
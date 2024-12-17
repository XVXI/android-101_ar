package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.concurrencyFormat
import com.transcode.smartcity101p2.extension.convertProductImage
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import kotlinx.android.synthetic.main.item_wishlist.view.*

class WishListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<GetProductResponse.GetProductResponseData>()
    private var listener: ClickItemListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_wishlist, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.wishlist_title.text = data.product_name
        holder.itemView.wishlist_prize.text = data.price?.concurrencyFormat() + " บาท"

        var image_url = ""
        if (!data.product_img_url_1.isNullOrEmpty()) {
            image_url = data.product_img_url_1.toString()
        } else if (!data.product_img_url_2.isNullOrEmpty()) {
            image_url = data.product_img_url_2.toString()
        } else if (!data.product_img_url_3.isNullOrEmpty()) {
            image_url = data.product_img_url_3.toString()
        } else if (!data.product_img_url_4.isNullOrEmpty()) {
            image_url = data.product_img_url_4.toString()
        }

        if (image_url.isNotEmpty()) {
            holder.itemView.wishlist_icon.load(image_url.convertProductImage(), R.mipmap.placeholder_image)
        } else {
            holder.itemView.wishlist_icon.load("", R.mipmap.placeholder_image)
        }

        holder.itemView.setOnClickListener {
            listener?.onClickDetail(data)
        }

        holder.itemView.wishlist_detail.setOnClickListener {
            listener?.onClickDetail(data)
        }

        holder.itemView.wishlist_delete.setOnClickListener {
            listener?.onClickDelete(data)
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<GetProductResponse.GetProductResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickItemListener) {
        this.listener = listener
    }

    interface ClickItemListener {
        fun onClickDetail(res: GetProductResponse.GetProductResponseData)
        fun onClickAddcart(res: GetProductResponse.GetProductResponseData)
        fun onClickDelete(res: GetProductResponse.GetProductResponseData)
    }
}
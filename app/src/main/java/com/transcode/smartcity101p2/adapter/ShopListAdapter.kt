package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.convertProductImage
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.buyapi.response.GetShopResponse
import kotlinx.android.synthetic.main.item_shop_list.view.*

class ShopListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<GetShopResponse.GetShopResponseData>()

    private var listener: ClickShopItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_shop_list, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.item_shop_text_title.text = data.shop_name
        holder.itemView.item_shop_text_type.text = data.ProductCategory?.pd_cat_name
        holder.itemView.item_shop_text_detail.text = data.shop_detail

        var image_url = ""
        if (!data.ShopOwner?.shop_owner_img.isNullOrEmpty()) {
            image_url = data.ShopOwner?.shop_owner_img.toString()
        }

        if (image_url.isNotEmpty()) {
            holder.itemView.item_shop_image.load(image_url.convertProductImage(), R.mipmap.placeholder_image)
        } else {
            holder.itemView.item_shop_image.load("", R.mipmap.placeholder_image)
        }

        holder.itemView.setOnClickListener {
            listener?.onClickShopItem(data)
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = GridLayoutManager(rv.context, 2)
        rv.adapter = this
    }

    fun setData(list: ArrayList<GetShopResponse.GetShopResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickShopItem) {
        this.listener = listener
    }

    interface ClickShopItem {
        fun onClickShopItem(res: GetShopResponse.GetShopResponseData)
    }
}
package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.concurrencyFormat
import com.transcode.smartcity101p2.extension.convertProductImage
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetShopResponse
import kotlinx.android.synthetic.main.item_shop_product_list.view.*

class ProductListAdapter(var context: Context) : AppBaseAdapter() {
    var list = arrayListOf<GetProductResponse.GetProductResponseData>()
    private var listener: ClickProductItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_shop_product_list, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.itemView.item_product_title.text = data.product_name
        holder.itemView.item_product_price.text = data.price?.concurrencyFormat() + " บาท"

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
            holder.itemView.item_product_image.load(image_url.convertProductImage(), R.mipmap.placeholder_image)
        } else {
            holder.itemView.item_product_image.load("", R.mipmap.placeholder_image)
        }

        if (data.is_delivery == "true") {
            holder.itemView.item_product_add.text = "Add Cart"
            holder.itemView.item_product_add.setTextColor(context.resources.getColor(R.color.white))
            holder.itemView.item_product_add.setBackgroundColor(context.resources.getColor(R.color.purple))
            holder.itemView.item_product_candeliver.visibility = View.VISIBLE
        } else {
            holder.itemView.item_product_add.text = "เฉพาะที่ร้าน"
            holder.itemView.item_product_add.setTextColor(context.resources.getColor(R.color.gray))
            holder.itemView.item_product_add.setBackgroundColor(context.resources.getColor(android.R.color.transparent))
            holder.itemView.item_product_candeliver.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            listener?.onClickProductItem(GetShopResponse.GetShopResponseData().apply {
                shop_id = data.shop_id
                shop_name = data.Shop?.shop_name
                ShopOwner = GetShopResponse.ShopOwner().apply {
                    shop_owner_img = data.Shop?.ShopOwner?.shop_owner_img
                }
            }, data)
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.HORIZONTAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<GetProductResponse.GetProductResponseData>) {
        this.list = list
    }

    fun setClickListener(listener: ClickProductItem) {
        this.listener = listener
    }

    interface ClickProductItem {
        fun onClickProductItem(shop: GetShopResponse.GetShopResponseData, res: GetProductResponse.GetProductResponseData)
    }
}
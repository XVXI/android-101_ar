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
import com.transcode.smartcity101p2.extension.convertDate
import com.transcode.smartcity101p2.extension.convertProductImage
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.buyapi.response.GetAllOrderResponse
import kotlinx.android.synthetic.main.item_order_list.view.*
import kotlinx.android.synthetic.main.item_order_product_list.view.*

class MarketSuccessOrderAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<GetAllOrderResponse.GetAllOrderResponseData>()

    private var listener: ClickCartItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_order_list, parent, false)
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val view = holder.itemView
        val data = list[position]
        view.item_order_date.text = data.create_datetime?.convertDate(25200000)
        view.item_order_code.text = data.order_code
        view.item_shop_name.text = data.Shop?.shop_name

        var all_price = 0

        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        for (d in data.OrderDetails) {
            val v = inflater.inflate(R.layout.item_order_product_list, null, false)
            v.layoutParams = lp
            view.item_layout_product.addView(v)

            v.item_cart_title.text = d.Product?.product_name
            v.item_cart_prize.text = d.unit_price?.concurrencyFormat() + " บาท x " + d.quantity

            var image_url = ""
            if (!d.Product?.product_img_url_1.isNullOrEmpty()) {
                image_url = d.Product?.product_img_url_1.toString()
            } else if (!d.Product?.product_img_url_2.isNullOrEmpty()) {
                image_url = d.Product?.product_img_url_2.toString()
            } else if (!d.Product?.product_img_url_3.isNullOrEmpty()) {
                image_url = d.Product?.product_img_url_3.toString()
            } else if (!d.Product?.product_img_url_4.isNullOrEmpty()) {
                image_url = d.Product?.product_img_url_4.toString()
            }

            if (image_url.isNotEmpty()) {
                v.item_cart_image.load(image_url.convertProductImage(), R.mipmap.placeholder_image)
            } else {
                v.item_cart_image.load("", R.mipmap.placeholder_image)
            }

            all_price += (d.quantity ?: "1").toInt() * (d.unit_price ?: "1").toInt()
        }

        val deliver_price = (data.delivery_price ?: "0").toInt()
        val d = deliver_price.toString().concurrencyFormat()
        view.item_deliver_price.text = "ค่าจัดส่ง $d บาท"
        all_price += deliver_price
        val a = all_price.toString().concurrencyFormat()
        view.item_all_price.text = "Total $a บาท"

        //                    0 = สั่ง
//                    1 = กำลังส่ง
//                    2 = ส่งเสร็จแล้ว
//                    3 = ลูกค้า confirm ว่าได้สินค้า
//                    4 = ยกเลิก

//                    0; // ลูกค้าสั่ง
//                    1; // ร้านค้า confirm
//                    2; // กำลังส่ง
//                    3; // ส่งเสร็จแล้ว xxx
//                    4; // ลูกค้า confirm ว่าได้สินค้า
//                    5; // ร้านยกเลิก
//                    6; // ลูกค้ายกเลิก

        view.item_confirm.text = when (data.order_status) {
            "0" -> {
                "ลูกค้าสั่ง"
            }
            "1" -> {
                "ร้านค้า confirm"
            }
            "2" -> {
                "กำลังส่ง"
            }
            "3" -> {
                "ส่งเสร็จแล้ว"
            }
            "4" -> {
                "ลูกค้า confirm ว่าได้สินค้า"
            }
            "5" -> {
                "ร้านยกเลิก"
            }
            "6" -> {
                "ลูกค้ายกเลิก"
            }
            else -> {
                "Status"
            }
        }

        view.item_layout_sum.visibility = View.GONE
        view.item_layout_status.visibility = View.GONE

    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<GetAllOrderResponse.GetAllOrderResponseData>) {
        this.list = list
    }

//    override fun getItemViewType(position: Int): Int {
//        return when (list[position]) {
//            is HashMap<*, *> -> {
//                TYPE_SHOP_NAME
//            }
//            else -> TYPE_PRODUCT_ITEM
//        }
//    }

    fun setCartClickListener(listener: ClickCartItem) {
        this.listener = listener
    }

    interface ClickCartItem {
        fun onClickCartItem()
//        fun onClickDeleteCartItem(res: GetProductResponse.GetProductResponseData)
    }
}
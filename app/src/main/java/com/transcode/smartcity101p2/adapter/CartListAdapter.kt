package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.concurrencyFormat
import com.transcode.smartcity101p2.extension.convertProductImage
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import kotlinx.android.synthetic.main.item_cart_product_list.view.*
import kotlinx.android.synthetic.main.item_shop_header.view.*

class CartListAdapter(var context: Context) : AppBaseAdapter() {

    var list = arrayListOf<Any>()

    companion object {
        const val TYPE_SHOP_NAME = 0
        const val TYPE_PRODUCT_ITEM = 1
    }

    private var listener: ClickCartItem? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = when (viewType) {
            TYPE_SHOP_NAME -> inflater.inflate(R.layout.item_shop_header, parent, false)
            else -> inflater.inflate(R.layout.item_cart_product_list, parent, false)
        }
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            TYPE_SHOP_NAME -> {
                val data = list[position] as HashMap<String, String>
                holder.itemView.shop_title.text = data["shop_name"]
                holder.itemView.shop_delivery_rate.text = "ค่าจัดส่ง " + data["delivery_rate"] + " บาท"
                val shop_id = data["shop_id"]
                var selectd_all = true
                for (d in list) {
                    if (d is GetProductResponse.GetProductResponseData) {
                        if (d.shop_id == shop_id) {
                            if (!d.is_selected) {
                                selectd_all = false
                            }
                        }
                    }
                }

                holder.itemView.shop_title.isChecked = selectd_all
                holder.itemView.shop_title.setOnClickListener {
                    var selectd_all = true
                    for (d in list) {
                        if (d is GetProductResponse.GetProductResponseData) {
                            if (d.shop_id == shop_id) {
                                if (!d.is_selected) {
                                    selectd_all = false
                                }
                            }
                        }
                    }

                    for (d in list) {
                        if (d is GetProductResponse.GetProductResponseData) {
                            if (d.shop_id == shop_id) {
                                d.is_selected = !selectd_all
                            }
                        }
                    }

                    listener?.onClickCartItem()
                    notifyDataSetChanged()
                }
            }
            else -> {

                val data = list[position] as GetProductResponse.GetProductResponseData
                val price = (data.price ?: "0").toInt() * (data.count ?: "1").toInt()

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
                    holder.itemView.item_cart_image.load(image_url.convertProductImage(), R.mipmap.placeholder_image)
                } else {
                    holder.itemView.item_cart_image.load("", R.mipmap.placeholder_image)
                }

                holder.itemView.item_cart_checkbox.isChecked = data.is_selected
                holder.itemView.delete_product.visibility = if (data.show_delete) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                holder.itemView.item_cart_title.text = data.product_name
                holder.itemView.item_cart_prize.text = price.toString().concurrencyFormat() + " บาท"
                holder.itemView.item_cart_deliver.text = "จัดส่งสินค้าฟรี"

                holder.itemView.item_cart_count.text = (data.count ?: "1").toString()

                holder.itemView.item_cart_inc.setOnClickListener {
                    var count = (data.count ?: "1").toInt()
                    if (count < 99) {
                        updateCartDB(1, data.product_id ?: "")

                        count += 1
                    } else {
                        count = 99
                    }

                    (list[position] as GetProductResponse.GetProductResponseData).count = count.toString()

                    val price = (data.price ?: "0").toInt() * count

                    holder.itemView.item_cart_count.text = (list[position] as GetProductResponse.GetProductResponseData).count.toString()
                    holder.itemView.item_cart_prize.text = price.toString().concurrencyFormat() + " บาท"

                    listener?.onClickCartItem()
                }

                holder.itemView.item_cart_dec.setOnClickListener {
                    var count = (data.count ?: "1").toInt()
                    if (count > 1) {
                        updateCartDB(-1, data.product_id ?: "")

                        count -= 1
                    } else {
                        count = 1
                    }

                    (list[position] as GetProductResponse.GetProductResponseData).count = count.toString()

                    val price = (data.price ?: "0").toInt() * count

                    holder.itemView.item_cart_count.text = (list[position] as GetProductResponse.GetProductResponseData).count.toString()
                    holder.itemView.item_cart_prize.text = price.toString().concurrencyFormat() + " บาท"

                    listener?.onClickCartItem()
                }

                holder.itemView.setOnClickListener {
                    (list[position] as GetProductResponse.GetProductResponseData).is_selected = !data.is_selected
                    listener?.onClickCartItem()
                    notifyDataSetChanged()
                }

                holder.itemView.item_cart_checkbox.setOnClickListener {
                    holder.itemView.performClick()
                }

                holder.itemView.setOnTouchListener(object : View.OnTouchListener {
                    var startX = 0f
                    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                        when (event?.action) {
                            MotionEvent.ACTION_DOWN -> {
                                startX = event.x
                                return false
                            }
                            MotionEvent.ACTION_MOVE -> {
                                if (startX - event.x > 30) {
                                    if (!holder.itemView.delete_product.isShown) {
                                        (list[position] as GetProductResponse.GetProductResponseData).show_delete = true
                                        notifyDataSetChanged()
                                    }
                                } else if (startX - event.x < -30) {
                                    if (holder.itemView.delete_product.isShown) {
                                        (list[position] as GetProductResponse.GetProductResponseData).show_delete = false
                                        notifyDataSetChanged()
                                    }
                                }
                                return true
                            }
                            else -> {
                                return false
                            }
                        }
                    }
                })

                holder.itemView.delete_product.setOnClickListener {
                    listener?.onClickDeleteCartItem(data)
                }
            }
        }
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setData(list: ArrayList<Any>) {
        this.list = list
    }

    override fun getItemViewType(position: Int): Int {
        return when (list[position]) {
            is HashMap<*, *> -> {
                TYPE_SHOP_NAME
            }
            else -> TYPE_PRODUCT_ITEM
        }
    }

    fun setCartClickListener(listener: ClickCartItem) {
        this.listener = listener
    }

    interface ClickCartItem {
        fun onClickCartItem()
        fun onClickDeleteCartItem(res: GetProductResponse.GetProductResponseData)
        fun updateTab()
    }

    fun updateCartDB(value: Int, product_id: String) {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val CitizenId = loginResponse.authority_info?.CitizenId ?: ""
        val cart_count = Hawk.get<Int>(Const.CART_COUNT + CitizenId) ?: 0
        Hawk.delete(Const.CART_COUNT + CitizenId)
        Hawk.put(Const.CART_COUNT + CitizenId, cart_count + value)

        val list = getCartList()

        for (data in list) {
            if (data.product_id == product_id) {
                var count = if (data.count.isNullOrEmpty()) {
                    0 + value
                } else {
                    (data.count ?: "").toInt() + value
                }

                data.count = count.toString()
            }
        }

        Hawk.delete(Const.CARTLIST + CitizenId)
        Hawk.put(Const.CARTLIST + CitizenId, list)

        listener?.updateTab()
    }

    private fun getCartList(): ArrayList<GetProductResponse.GetProductResponseData> {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val CitizenId = loginResponse.authority_info?.CitizenId ?: ""
        return if (Hawk.get<ArrayList<GetProductResponse.GetProductResponseData>>(Const.CARTLIST + CitizenId) != null) {
            Hawk.get<ArrayList<GetProductResponse.GetProductResponseData>>(Const.CARTLIST + CitizenId)
        } else {
            arrayListOf()
        }
    }
}
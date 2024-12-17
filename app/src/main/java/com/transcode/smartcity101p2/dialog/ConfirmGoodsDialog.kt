package com.transcode.smartcity101p2.dialog

import android.content.Context
import android.os.Bundle
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
import kotlinx.android.synthetic.main.dialog_confirm_goods.view.*
import kotlinx.android.synthetic.main.item_order_product_list.view.*

class ConfirmGoodsDialog(context: Context, var item: GetAllOrderResponse.GetAllOrderResponseData) : CoreDialog(context) {

    var b: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initLayout()
    }

    private fun initLayout() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.dialog_confirm_goods, null, false)
        setContentView(b)

        b?.dl_close?.setOnClickListener {
            dismiss()
        }

        b?.let { view ->
            view.dl_order_date.text = item.create_datetime?.convertDate()
            view.dl_order_code.text = item.order_code
            view.dl_shop_name.text = item.Shop?.shop_name
            view.dl_address.text = item.delivery_address

            if (view.dl_shop_name.text.isNullOrEmpty() && view.dl_address.text.isNullOrEmpty()) {
                view.dl_layout_address.visibility = View.GONE
            }

            var all_price = 0
            val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            for (data in item.OrderDetails) {
                val v = inflater.inflate(R.layout.item_order_product_list, null, false)
                v.layoutParams = lp
                view.dl_item.addView(v)

                v.item_cart_title.text = data.Product?.product_name
                v.item_cart_prize.text = data.unit_price?.concurrencyFormat() + " บาท x " + data.quantity

                v.item_cart_title.setTextColor(context.resources.getColor(R.color.white))
                v.item_cart_prize.setTextColor(context.resources.getColor(R.color.white))

                all_price += (data.quantity ?: "1").toInt() * (data.unit_price ?: "1").toInt()

                var image_url = ""
                if (!data.Product?.product_img_url_1.isNullOrEmpty()) {
                    image_url = data.Product?.product_img_url_1.toString()
                } else if (!data.Product?.product_img_url_2.isNullOrEmpty()) {
                    image_url = data.Product?.product_img_url_2.toString()
                } else if (!data.Product?.product_img_url_3.isNullOrEmpty()) {
                    image_url = data.Product?.product_img_url_3.toString()
                } else if (!data.Product?.product_img_url_4.isNullOrEmpty()) {
                    image_url = data.Product?.product_img_url_4.toString()
                }

                if (image_url.isNotEmpty()) {
                    v.item_cart_image.load(image_url.convertProductImage(), R.mipmap.placeholder_image)
                } else {
                    v.item_cart_image.load("", R.mipmap.placeholder_image)
                }
            }

            val deliver_price = (item.delivery_price ?: "0").toInt()
            val d = deliver_price.toString().concurrencyFormat()
            view.dl_deliver.text = "ค่าจัดส่ง $d บาท"
            all_price += deliver_price
            val a = all_price.toString().concurrencyFormat()
            view.dl_total.text = "Total $a บาท"
        }
    }

    fun getRootView(): View? = b
}
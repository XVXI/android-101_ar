package com.transcode.smartcity101p2.fragment

import android.os.Bundle
import android.text.InputFilter
import android.view.*
import android.widget.Toast
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.MarketHomeActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.CartListAdapter
import com.transcode.smartcity101p2.contract.MarketCartFragmentContract
import com.transcode.smartcity101p2.dialog.MarketConfirmBuyDialog
import com.transcode.smartcity101p2.extension.concurrencyFormat
import com.transcode.smartcity101p2.extension.convertProductImage
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.buyapi.response.AddressResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import com.transcode.smartcity101p2.presenter.MarketCartFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.dialog_market_confirm_cart.view.*
import kotlinx.android.synthetic.main.fragment_market_cart.*
import kotlinx.android.synthetic.main.item_cart_product_list2.view.*
import kotlinx.android.synthetic.main.item_shop_header.view.*


class MarketCartFragment : CoreFragment(), CartListAdapter.ClickCartItem, MarketCartFragmentContract.View {

    companion object {
        fun newInstance(mode: Int, title: String): MarketCartFragment {
            return MarketCartFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var adapter: CartListAdapter
    var marketConfirmBuyDialog: MarketConfirmBuyDialog? = null
    lateinit var presenter: MarketCartFragmentPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_market_cart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        presenter = MarketCartFragmentPresenter(this)
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("ตะกร้าสินค้า")
        appBar.setTitleColor(resources.getColor(R.color.white))
        appBar.setHeaderBackground(R.drawable.bg_gradient_purple)
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                (this as MarketHomeActivity).moveTo(0)
            }
        }

        context?.let {
            adapter = CartListAdapter(it)
            adapter.setRecyclerView(recycler_cart)
            adapter.setData(getOrderList(getCartList()))
            adapter.notifyDataSetChanged()

            adapter.setCartClickListener(this)

            setSelectedAll()
        }

        cart_se_de_select_all_check.setOnClickListener {
            var selected_all = true
            for (data in adapter.list) {
                if (data is GetProductResponse.GetProductResponseData) {
                    if (!data.is_selected) {
                        selected_all = false
                    }
                }
            }

            for (data in adapter.list) {
                if (data is GetProductResponse.GetProductResponseData) {
                    data.is_selected = !selected_all
                }
            }

            setSelectedAll()

            adapter.notifyDataSetChanged()
        }

        updateSelectedAll()
    }

    private fun setSelectedAll() {
        var selected_all = true
        var all_price = 0
        val selectedOrder = arrayListOf<GetProductResponse.GetProductResponseData>()
        for (data in adapter.list) {
            if (data is GetProductResponse.GetProductResponseData) {
                if (!data.is_selected) {
                    selected_all = false
                } else {
                    selectedOrder.add(data)
                    val count = (data.count ?: "1").toInt()
                    val price = (data.price ?: "0").toInt() * count
                    all_price += price
                }
            }
        }

        for (data in adapter.list) {
            if (data is HashMap<*, *>) {
                var inList = false
                for (order in selectedOrder) {
                    if (order.shop_id == data["shop_id"]) {
                        inList = true
                    }
                }
                if (inList) {
                    val delivery_rate = ((data as HashMap<String, String>)["delivery_rate"] ?: "0")
                            .toInt()
                    all_price += delivery_rate
                }
            }
        }

        text_all_price.text = all_price.toString().concurrencyFormat() + " บาท"
        if (all_price > 0) {
            button_buy.isEnabled = true
            button_buy.alpha = 1.0f
        } else {
            button_buy.isEnabled = false
            button_buy.alpha = 0.7f
        }

        button_buy.setOnClickListener {
            //show dialog
            showConfirmDialog(all_price.toString(), selectedOrder)
        }

        cart_se_de_select_all_check.isChecked = selected_all
    }

    fun updateView() {
        recycler_cart?.let {
            adapter.setData(getOrderList(getCartList()))
            adapter.notifyDataSetChanged()

            setSelectedAll()
            updateSelectedAll()
        }
    }

    private fun updateSelectedAll() {
        var count = 0

        for (data in adapter.list) {
            if (data is GetProductResponse.GetProductResponseData) {
                count += 1
            }
        }

        cart_se_de_select_all_check.text = "เลือกรายการทั้งหมด ($count)"
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

    private fun getOrderList(list: ArrayList<GetProductResponse.GetProductResponseData>): ArrayList<Any> {
        if (list.isEmpty()) {
            val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            val CitizenId = loginResponse.authority_info?.CitizenId ?: ""
            Hawk.delete(Const.CART_COUNT + CitizenId)
            Hawk.put(Const.CART_COUNT + CitizenId, 0)
        }

        val shoplist = arrayListOf<HashMap<String, String>>()
        val arlist = arrayListOf<Any>()

        for (data in list) {
            val h = hashMapOf<String, String>()
            h["shop_id"] = data.shop_id ?: ""
            h["shop_name"] = data.shop_name ?: ""
            h["delivery_rate"] = data.Shop.delivery_rate ?: "0"

            var inlist = false
            for (shop in shoplist) {
                if (shop["shop_id"] == h["shop_id"]) {
                    inlist = true
                }
            }
            if (!inlist) {
                shoplist.add(h)
            }
        }

        for (h in shoplist) {
            arlist.add(h)
            for (l in list) {
                if (l.shop_id == h["shop_id"]) {
                    arlist.add(l)
                }
            }
        }

        return arlist
    }

    override fun onClickCartItem() {
        setSelectedAll()
    }

    override fun onClickDeleteCartItem(res: GetProductResponse.GetProductResponseData) {
        val cartList = getCartList()

        var in_cart_list = false
        var d: GetProductResponse.GetProductResponseData? = null

        for (data in cartList) {
            if (res.product_id == data.product_id) {
                d = data
                in_cart_list = true
            }
        }

        if (in_cart_list) {
            d?.let { data ->
                cartList.remove(data)
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val CitizenId = loginResponse.authority_info?.CitizenId ?: ""
        Hawk.delete(Const.CARTLIST + CitizenId)
        Hawk.put(Const.CARTLIST + CitizenId, cartList)

        val cart_count = Hawk.get<Int>(Const.CART_COUNT + CitizenId) ?: 0
        var count = (res.count ?: "0").toInt()
        if (cart_count - count >= 0) {
            Hawk.delete(Const.CART_COUNT + CitizenId)
            Hawk.put(Const.CART_COUNT + CitizenId, cart_count - count)

            activity?.let { ac ->
                (ac as MarketHomeActivity).updateCartTab()
            }
        }

        updateView()
    }

    override fun updateTab() {
        activity?.let { ac ->
            (ac as MarketHomeActivity).updateCartTab()
        }
    }

    private fun showConfirmDialog(price: String, selectedOrder: ArrayList<GetProductResponse.GetProductResponseData>) {
        context?.let {
            marketConfirmBuyDialog = MarketConfirmBuyDialog(it)
            marketConfirmBuyDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            marketConfirmBuyDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

            val window = marketConfirmBuyDialog?.window
            val wlp = window?.attributes

            wlp?.gravity = Gravity.BOTTOM
            wlp?.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
            window?.attributes = wlp

            marketConfirmBuyDialog?.show()

            marketConfirmBuyDialog?.setPrice(price)

            marketConfirmBuyDialog?.b?.let { view ->
                val shopList = arrayListOf<String>()
                val shopDelivery = hashMapOf<String, String>()
                var count = 0
                for (its in selectedOrder) {
                    val c = (its.count ?: "0").toInt()
                    count += c

                    var inShopList = false
                    for (s in shopList) {
                        if (s == its.shop_name) {
                            inShopList = true
                        }
                    }

                    if (!inShopList) {
                        shopList.add(its.shop_name ?: "")
                        shopDelivery[its.shop_name ?: ""] = its.Shop.delivery_rate ?: ""
                    }
                }

                val items = if (count > 1) {
                    "ITEMS"
                } else {
                    "ITEM"
                }
                val sum = "ORDER SUMMARY( $count $items)"
                view.text_sum.text = sum

                val inflater = LayoutInflater.from(it)
                for (shopname in shopList) {
                    val header = inflater.inflate(R.layout.item_shop_header, null, false)
                    header.shop_title.text = shopname
                    header.shop_delivery_rate.text = "ค่าจัดส่ง " + shopDelivery[shopname] + " บาท"
                    header.shop_title.buttonDrawable = null
                    view.layout_sum.addView(header)
                    for (ii in selectedOrder) {
                        if (ii.shop_name == shopname) {
                            val irow = inflater.inflate(R.layout.item_cart_product_list2, null, false)
                            view.layout_sum.addView(irow)

                            irow.item_cart_checkbox.visibility = View.GONE

                            val p = (ii.price ?: "0").toInt() * (ii.count ?: "1").toInt()

                            var image_url = ""
                            if (!ii.product_img_url_1.isNullOrEmpty()) {
                                image_url = ii.product_img_url_1.toString()
                            } else if (!ii.product_img_url_2.isNullOrEmpty()) {
                                image_url = ii.product_img_url_2.toString()
                            } else if (!ii.product_img_url_3.isNullOrEmpty()) {
                                image_url = ii.product_img_url_3.toString()
                            } else if (!ii.product_img_url_4.isNullOrEmpty()) {
                                image_url = ii.product_img_url_4.toString()
                            }

                            if (image_url.isNotEmpty()) {
                                irow.item_cart_image.load(image_url.convertProductImage(), R.mipmap.placeholder_image)
                            } else {
                                irow.item_cart_image.load("", R.mipmap.placeholder_image)
                            }

                            irow.item_cart_title.text = ii.product_name
                            irow.item_cart_prize.text = p.toString().concurrencyFormat() + " บาท"
                            irow.item_cart_deliver.text = "จัดส่งสินค้าฟรี"

                            irow.item_cart_count.text = "จำนวน : " + ii.count.toString()
                        }
                    }
                }

                val defAddress = Hawk.get<AddressResponse.AddressResponseData>("DEF_ADDRESS")
                        ?: AddressResponse.AddressResponseData()
                view.dl_name.setText(defAddress.name ?: "")
                view.dl_address.setText(defAddress.address ?: "")
                view.dl_tel.setText(defAddress.tel ?: "")

                val filter = InputFilter { source, start, end, dest, dstart, dend ->
                    for (i in start until end) {
                        if (!isNumberFilter(source[i])) {
                            return@InputFilter ""
                        }
                    }
                    null
                }
                view.dl_tel.filters = arrayOf(filter)

                view.dl_button_buy.setOnClickListener { bt ->
                    if (view.dl_name.text.isNullOrEmpty()) {
                        Toast.makeText(bt.context, "โปรดใส่ชื่อ", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    if (view.dl_address.text.isNullOrEmpty()) {
                        Toast.makeText(bt.context, "โปรดใส่ที่อยู่", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    if (view.dl_tel.text.isNullOrEmpty()) {
                        Toast.makeText(bt.context, "โปรดใส่เบอร์โทรศัพท์", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    } else if (view.dl_tel.text.length <= 9) {
                        Toast.makeText(bt.context, "โปรดใส่เบอร์โทรศัพท์", Toast.LENGTH_LONG).show()
                        return@setOnClickListener
                    }

                    val geo = marketConfirmBuyDialog?.getLatLng()?.let { ln ->
                        ln.latitude.toString() + ", " + ln.longitude.toString()
                    } ?: kotlin.run {
                        "16.054158, 103.6501564"
                    }
                    val name = view.dl_name.text.toString()
                    val address = view.dl_address.text.toString()
                    val tel = view.dl_tel.text.toString()
                    presenter.addOrder(geo, name, address, tel, selectedOrder)
                    marketConfirmBuyDialog?.dismiss()
                }
            }
        }
    }


    override fun addOrderError() {

    }

    override fun addOrderSuccess(selectedOrder: ArrayList<GetProductResponse.GetProductResponseData>) {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val CitizenId = loginResponse.authority_info?.CitizenId ?: ""

        val cartList = getCartList()
        var count = 0
        val cart_count = Hawk.get<Int>(Const.CART_COUNT + CitizenId) ?: 0
        for (data in selectedOrder) {
            var d: GetProductResponse.GetProductResponseData? = null
            for (data2 in cartList) {
                if (data.product_id == data2.product_id) {
                    d = data2
                }
            }
            d?.let {
                cartList.remove(it)
            }

            count += (data.count ?: "0").toInt()
        }

        Hawk.delete(Const.CARTLIST + CitizenId)
        Hawk.put(Const.CARTLIST + CitizenId, cartList)

        if (cart_count - count >= 0) {
            Hawk.delete(Const.CART_COUNT + CitizenId)
            Hawk.put(Const.CART_COUNT + CitizenId, cart_count - count)

            activity?.let { ac ->
                (ac as MarketHomeActivity).updateCartTab()
            }
        }

        updateView()

        activity?.let {
            (it as MarketHomeActivity).moveTo(2)
        }
    }

    private fun isNumberFilter(c: Char): Boolean {
        val l = "1234567890"
        for (ch in l) {
            if (c == ch) {
                return true
            }
        }
        return false
    }
}
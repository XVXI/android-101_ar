package com.transcode.smartcity101p2.fragment

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.LinearLayout
import android.widget.Toast
import com.transcode.smartcity101p2.MarketHomeActivity
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.orhanobut.hawk.Hawk
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.Style
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.*
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.MarketHomeFragmentContract
import com.transcode.smartcity101p2.dialog.*
import com.transcode.smartcity101p2.extension.concurrencyFormat
import com.transcode.smartcity101p2.extension.convertProductImage
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.CommonResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.SendOrderRequest
import com.transcode.smartcity101p2.model.buyapi.response.CateListResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import com.transcode.smartcity101p2.model.buyapi.response.GetShopResponse
import com.transcode.smartcity101p2.presenter.MarketHomeFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout3
import kotlinx.android.synthetic.main.appbar_main3.view.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.dialog_product.view.*
import kotlinx.android.synthetic.main.dialog_text.view.*
import kotlinx.android.synthetic.main.fragment_market_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class MarketHomeFragment : CoreFragment(), MarketHomeFragmentContract.View, ShopListAdapter.ClickShopItem, ShopProductAdapter.ClickProductItem, OnMapReadyCallback, ProductTypeListAdapter.ClickProductItem {

    companion object {
        fun newInstance(mode: Int, title: String): MarketHomeFragment {
            return MarketHomeFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: MarketHomeFragmentPresenter
    lateinit var adapter: ShopListAdapter
    lateinit var shop_product_adapter: ShopProductAdapter
    lateinit var cate_adapter: CateArrayAdapter
    val SHOP_LIST = 0
    val SHOP_MAP = 1
    var shop_type = SHOP_LIST

    val stackPage = arrayListOf<Int>()
    val page_recc = 0
    val pageShopList = 2
    val pageShop = 3

    lateinit var adapterTypeList: ProductTypeListAdapter

    var marketCallPhoneDialog: MarketCallPhoneDialog? = null
    var marketShopLocationDialog: MarketShopLocationDialog? = null
    var marketTextDialog: MarketTextDialog? = null

    private var savedInstanceState: Bundle? = null
    private var mapboxMap: MapboxMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.savedInstanceState = savedInstanceState
        return inflater.inflate(R.layout.fragment_market_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter = MarketHomeFragmentPresenter(this)

        context?.let {
            adapter = ShopListAdapter(it)
            adapter.setRecyclerView(recycler_search)
            adapter.setClickListener(this)
            adapter.notifyDataSetChanged()

            shop_product_adapter = ShopProductAdapter(it)
            shop_product_adapter.setRecyclerView(recycler_shop_product)
            shop_product_adapter.setClickListener(this)
            shop_product_adapter.notifyDataSetChanged()

            adapterTypeList = ProductTypeListAdapter(it)
            adapterTypeList.setRecyclerView(recycler_type_list)
            adapterTypeList.setMockup()
            adapterTypeList.notifyDataSetChanged()
            adapterTypeList.setClickListener(this)
        }

        initView()
    }

    private fun initView() {

        stackPage.add(page_recc)

        val appBar = appbar as CustomAppBarLayout3
        appBar.setTitle("")
        appBar.setHeaderBackground(R.drawable.bg_gradient_purple)
        appBar.leftBt.setOnClickListener {
            checkBack()
        }

        button_menu_list.load("", R.mipmap.market_icon_list_active)

        button_menu_list.setOnClickListener {
            shop_type = SHOP_LIST
            button_menu_map.load("", R.mipmap.market_icon_map_inactive)
            button_menu_list.load("", R.mipmap.market_icon_list_active)

            recycler_search.visibility = View.VISIBLE
            map_search.visibility = View.GONE
        }
        button_menu_map.setOnClickListener {
            shop_type = SHOP_MAP
            button_menu_map.load("", R.mipmap.market_icon_map_active)
            button_menu_list.load("", R.mipmap.market_icon_list_inactive)

            recycler_search.visibility = View.GONE
            map_search.visibility = View.VISIBLE
        }

        edit_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //search
                search(edit_search.text.toString())
                true
            } else {
                false
            }
        }

        icon_search.setOnClickListener {
            //search
            search(edit_search.text.toString())
        }

        presenter.getCate()
        getAllType()
    }

    var keyword = ""
    private fun search(key: String) {
        if (key.isEmpty()) {
            return
        } else if (key == keyword) {
            return
        }
        val cate = if (cate_adapter.list.size > 0) {
            cate_adapter.list[list_cate.selectedItemPosition].pd_online_cat_id.toString()
        } else {
            ""
        }

        hideSoftKeyboard()
        mapboxMap?.let {
            presenter.getShopByKey(key, cate)
        } ?: kotlin.run {
            keyword = key
            layout_search.visibility = View.VISIBLE
            map_search.visibility = View.VISIBLE
            layout_shop_show_type.visibility = View.GONE
            try {
                map_search.onCreate(savedInstanceState)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            map_search.getMapAsync(this)
        }
    }

    private fun getAllType() {
        presenter.getBestSeller()
        presenter.getPopular()
        presenter.getNewProduct()
    }

    override fun onMapReady(map: MapboxMap) {
        map.setStyle(Style.Builder().fromUri(Const.MAP_STYLE_URI), Style.OnStyleLoaded {
            map_search.visibility = View.GONE
        })
        mapboxMap = map

        map?.infoWindowAdapter = MyInfoWindowAdapter()

        if (keyword.isEmpty()) {
            return
        }
        val cate = if (cate_adapter.list.size > 0) {
            cate_adapter.list[list_cate.selectedItemPosition].pd_online_cat_id.toString()
        } else {
            ""
        }
        presenter.getShopByKey(keyword, cate)
        keyword = ""
    }

    override fun updateShopList(list: ArrayList<GetShopResponse.GetShopResponseData>) {
        if (stackPage[stackPage.size - 1] != pageShopList) {
            stackPage.add(pageShopList)
        }

        hideAll()
        all_list.visibility = View.VISIBLE
        layout_search.visibility = View.VISIBLE
        layout_shop_show_type.visibility = View.VISIBLE
        when (shop_type) {
            SHOP_MAP -> {
                map_search.visibility = View.VISIBLE
            }
            else -> {
                recycler_search.visibility = View.VISIBLE
            }
        }

//        if (list.isNotEmpty()) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()
//        }

        mapboxMap?.let {
            for (mark in it.markers) {
                it.removeMarker(mark)
            }
        }
        //update map
        if (list.isNotEmpty()) {
            val latLng = LatLng((list[0].geo_location?.coordinates?.get(0)
                    ?: "0").toDouble(), (list[0].geo_location?.coordinates?.get(1)
                    ?: "0").toDouble())
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
            mapboxMap?.moveCamera(cameraUpdate)
        }

        for (data in list) {
            setMarker(LatLng((data.geo_location?.coordinates?.get(0)
                    ?: "0").toDouble(), (data.geo_location?.coordinates?.get(1)
                    ?: "0").toDouble()), data.shop_name ?: "")
        }

        mapboxMap?.setOnInfoWindowClickListener {
            for (data in list) {
                val ln = LatLng((data.geo_location?.coordinates?.get(0)
                        ?: "0").toDouble(), (data.geo_location?.coordinates?.get(1)
                        ?: "0").toDouble())
                if ((it.title == data.shop_name) &&
                        (ln.latitude == it.position.latitude) &&
                        (ln.longitude == it.position.longitude)) {
                    onClickShopItem(data)
                    break
                }
            }
            true
        }
    }

    private fun setMarker(lngYou: LatLng, title: String) {
        val marker = MarkerOptions()
                .title(title)
                .position(LatLng(lngYou.latitude, lngYou.longitude))
                .icon(bitmapDescriptorFromVector(context, R.mipmap.icon_pin_101))
        mapboxMap?.let {
            it.addMarker(marker)

            val ll = LatLng((adapter.list[0].geo_location?.coordinates?.get(0)
                    ?: "0").toDouble(), (adapter.list[0].geo_location?.coordinates?.get(1)
                    ?: "0").toDouble())

            for (m in it.markers) {
                if ((ll.latitude == m.position.latitude)
                        && (ll.longitude == m.position.longitude)
                        && (adapter.list[0].shop_name == title)) {
                    it.selectMarker(m)
                }
            }

        }


    }

    override fun updateShopProduct(shop: GetShopResponse.GetShopResponseData, list: ArrayList<GetProductResponse.GetProductResponseData>) {
        hideAll()
        stackPage.add(pageShop)
        layout_shop_detail.visibility = View.VISIBLE

        layout_shop_detail_title.text = shop.shop_name
        layout_shop_detail_text.text = shop.shop_detail
        layout_shop_detail_type.text = shop.ProductCategory?.pd_cat_name

        var image_url = ""
        if (!shop.ShopOwner?.shop_owner_img.isNullOrEmpty()) {
            image_url = shop.ShopOwner?.shop_owner_img.toString()
        }

        if (image_url.isNotEmpty()) {
            layout_shop_detail_image.load(image_url.convertProductImage(), R.mipmap.market_icon_owner)
        } else {
            layout_shop_detail_image.load("", R.mipmap.market_icon_owner)
        }

        shop_product_adapter.setData(shop, list)
        shop_product_adapter.notifyDataSetChanged()
    }

    fun showShop(shop_id: String) {
//        for (data in adapter.list) {
//            if (data.shop_id == shop_id) {
//                onClickShopItem(data)
//                break
//            }
//        }
        onClickShopItem(GetShopResponse.GetShopResponseData().apply {
            this.shop_id = shop_id
        })
    }

    override fun onClickShopItem(res: GetShopResponse.GetShopResponseData) {
        res.shop_id?.let {
            shop_product_adapter.setData(res, arrayListOf())
            shop_product_adapter.notifyDataSetChanged()
            presenter.getShopDetail(res.shop_id ?: "")
        }
    }

    override fun getShopSuccess(res: GetShopResponse.GetShopResponseData) {
        layout_shop_detail_map.setOnClickListener {
            showShopLocationDialog(res)
        }

        layout_shop_detail_tel.setOnClickListener {
            showCallDialog(res.shop_name ?: "", res.ShopOwner?.tel ?: "0000000000")
        }
        presenter.getShopProducts(res, res.shop_id ?: "")
    }

    override fun getShopError() {
        showTextDialog("ไม่พบข้อมูลสินค้านี้ในระบบ")
    }

    override fun onClickProductItem(shop: GetShopResponse.GetShopResponseData, res: GetProductResponse.GetProductResponseData) {
        context?.let {
            sendOrder(res.product_id ?: "")
            res.shop_name = shop.shop_name
            val productDialog = ProductDialog(it)
            productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            productDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            productDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            productDialog.show()

            presenter.getProductDetail(res.product_id ?: "")

            if (res.Shop.delivery_rate.isNullOrEmpty() && !shop.delivery_rate.isNullOrEmpty()) {
                res.Shop.delivery_rate = shop.delivery_rate
            }

            var wishlistClicked = false
            var count = 1

            productDialog.getView()?.let { view ->

                val imagelist = arrayListOf<String>()
                if (!res.product_img_url_1.isNullOrEmpty()) {
                    imagelist.add(res.product_img_url_1.toString().convertProductImage())
                }
                if (!res.product_img_url_2.isNullOrEmpty()) {
                    imagelist.add(res.product_img_url_2.toString().convertProductImage())
                }
                if (!res.product_img_url_3.isNullOrEmpty()) {
                    imagelist.add(res.product_img_url_3.toString().convertProductImage())
                }
                if (!res.product_img_url_4.isNullOrEmpty()) {
                    imagelist.add(res.product_img_url_4.toString().convertProductImage())
                }

                val photoAdapter = PhotosAdapter2(it)
                view.photos_viewpager.adapter = photoAdapter
                view.tab_layout.setupWithViewPager(view.photos_viewpager, true)
                photoAdapter.setData(imagelist)
                photoAdapter.notifyDataSetChanged()

                for (i in 0 until photoAdapter.count step 1) {
                    val tab = LayoutInflater.from(context).inflate(R.layout.custom_tab2, null)
                    tab.setBackgroundResource(R.drawable.tab_selector)
                    view.tab_layout.getTabAt(i)?.customView = tab
                    view.tab_layout.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(it.resources.getDimensionPixelSize(R.dimen.dimen_tab_width), LinearLayout.LayoutParams.MATCH_PARENT)
                }

                var shop_url = ""
                if (!shop.ShopOwner?.shop_owner_img.isNullOrEmpty()) {
                    shop_url = shop.ShopOwner?.shop_owner_img.toString()
                }
                view.dl_shop_image.load(shop_url.convertProductImage(), R.mipmap.market_icon_owner)

                if (res.is_delivery == "true") {
                    view.dl_product_layout_count.visibility = View.VISIBLE
                    view.dl_product_layout_deliver.visibility = View.VISIBLE
                    view.dl_product_add.visibility = View.VISIBLE
                    view.dl_product_deliver.text = "มีบริการจัดส่ง"
                } else {
                    view.dl_product_layout_count.visibility = View.INVISIBLE
                    view.dl_product_layout_deliver.visibility = View.INVISIBLE
                    view.dl_product_add.visibility = View.GONE
                    view.dl_product_deliver.text = "เฉพาะทานที่ร้าน"
                }

                view.dl_shop_name.text = shop.shop_name
                view.dl_shop_detail.setOnClickListener {
                    productDialog.dismiss()
                    onClickShopItem(shop)
                }

                view.dl_product_title.text = res.product_name
                view.dl_product_price.text = res.price?.concurrencyFormat() + " บาท"

                view.dl_product_detail.text = res.product_detail

                val wishList = getWishList()

                var in_wish_list = false

                for (data in wishList) {
                    if (res.product_id == data.product_id) {
                        in_wish_list = true
                    }
                }

                if (in_wish_list) {
                    view.dl_product_wishlist.load("", R.mipmap.market_icon_fav_active)
                } else {
                    view.dl_product_wishlist.load("", R.mipmap.market_icon_fav_inactive)
                }

                view.dl_product_wishlist.setOnClickListener {

                    if (isGuest()) {
                        showPopup()
                        return@setOnClickListener
                    }

                    wishlistClicked = true

                    val wishList = getWishList()

                    var d: GetProductResponse.GetProductResponseData? = null
                    var in_wish_list = false

                    for (data in wishList) {
                        if (res.product_id == data.product_id) {
                            d = data
                            in_wish_list = true
                        }
                    }

                    if (in_wish_list) {
                        d?.let { data ->
                            wishList.remove(data)
                            view.dl_product_wishlist.load("", R.mipmap.market_icon_fav_inactive)
                        }
                    } else {
                        wishList.add(res)
                        view.dl_product_wishlist.load("", R.mipmap.market_icon_fav_active)
                    }

                    val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                    val CitizenId = loginResponse.authority_info?.CitizenId ?: ""
                    Hawk.delete(Const.WISHLIST + CitizenId)
                    Hawk.put(Const.WISHLIST + CitizenId, wishList)
                }

                view.dl_dec.setOnClickListener {
                    if (count > 1) {
                        count -= 1
                    } else {
                        count = 1
                    }

                    view.dl_count.text = count.toString()

                    val price = count * (res.price ?: "1").toInt()
                    view.dl_product_price.text = price.toString().concurrencyFormat() + " บาท"
                }

                view.dl_inc.setOnClickListener {
                    if (count < 99) {
                        count += 1
                    } else {
                        count = 99
                    }

                    view.dl_count.text = count.toString()

                    val price = count * (res.price ?: "1").toInt()
                    view.dl_product_price.text = price.toString().concurrencyFormat() + " บาท"
                }

                view.dl_count.text = count.toString()

                productDialog.setOnClickOKListener(View.OnClickListener {
                    if (isGuest()) {
                        Toast.makeText(it.context, "โปรดเข้าสู่ระบบก่อนทำรายการ", Toast.LENGTH_LONG).show()
                        return@OnClickListener
                    }

                    val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                    val CitizenId = loginResponse.authority_info?.CitizenId ?: ""

                    val cart_count = Hawk.get<Int>(Const.CART_COUNT + CitizenId) ?: 0
                    Hawk.delete(Const.CART_COUNT + CitizenId)
                    Hawk.put(Const.CART_COUNT + CitizenId, cart_count + count)

                    val cartList = getCartList()

                    var in_cart_list = false
                    var d: GetProductResponse.GetProductResponseData? = null

                    for (data in cartList) {
                        if (res.product_id == data.product_id) {
                            d = data
                            in_cart_list = true
                            count += (data.count ?: "1").toInt()
                        }
                    }

                    if (in_cart_list) {
                        d?.let { data ->
                            cartList.remove(data)
                            data.count = count.toString()
                            data.shop_name = res.shop_name
                            cartList.add(data)
                        }
                    } else {
                        res.count = count.toString()
                        cartList.add(res)
                    }

                    Hawk.delete(Const.CARTLIST + CitizenId)
                    Hawk.put(Const.CARTLIST + CitizenId, cartList)

                    activity?.let { ac ->
                        (ac as MarketHomeActivity).updateCartTab()
                    }

                    productDialog.dismiss()
                })

                productDialog.setOnClickCancelListener(View.OnClickListener {
                    productDialog.dismiss()
                })

                productDialog.setOnDismissListener {
                    val wishList = getWishList()

                    var in_wish_list = false

                    for (data in wishList) {
                        if (res.product_id == data.product_id) {
                            in_wish_list = true
                        }
                    }
                    if (wishlistClicked && in_wish_list) {
                        presenter.addToWishList(res.product_id ?: "")
                    }
                }
            }
        }
    }

    private fun getWishList(): ArrayList<GetProductResponse.GetProductResponseData> {
        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val CitizenId = loginResponse.authority_info?.CitizenId ?: ""
        return if (Hawk.get<ArrayList<GetProductResponse.GetProductResponseData>>(Const.WISHLIST + CitizenId) != null) {
            Hawk.get<ArrayList<GetProductResponse.GetProductResponseData>>(Const.WISHLIST + CitizenId)
        } else {
            arrayListOf()
        }
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

    private fun hideAll() {
        recycler_type_list.visibility = View.GONE
        layout_search.visibility = View.GONE
        all_list.visibility = View.GONE

        recycler_search.visibility = View.GONE
        map_search.visibility = View.GONE
        layout_shop_detail.visibility = View.GONE
        layout_shop_show_type.visibility = View.GONE
    }

    internal inner class MyInfoWindowAdapter : MapboxMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {
            return null
        }
    }

    private fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): Icon {
        val w = (100 * 0.8).roundToInt()
        val h = (120 * 0.8).roundToInt()

        val vectorDrawable = ContextCompat.getDrawable(context!!, vectorResId)
        vectorDrawable!!.setBounds(0, 0, w, h)
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val resizeBitmap = Bitmap.createScaledBitmap(bitmap, w, h, false)
        val canvas = Canvas(resizeBitmap)
        vectorDrawable.draw(canvas)
        return IconFactory.getInstance(context).fromBitmap(resizeBitmap)
    }

    private fun showCallDialog(title: String, phone: String) {
        context?.let {
            val con = it
            marketCallPhoneDialog = MarketCallPhoneDialog(it)
            marketCallPhoneDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            marketCallPhoneDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            marketCallPhoneDialog?.show()
            marketCallPhoneDialog?.setTitle(getString(R.string.callphone_dialog_header))
            marketCallPhoneDialog?.setMessage(phone)
            marketCallPhoneDialog?.setOnClickOKListener(View.OnClickListener {
                marketCallPhoneDialog?.dismiss()
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                con.startActivity(intent)
            })
            marketCallPhoneDialog?.setOnClickCancelListener(View.OnClickListener {
                marketCallPhoneDialog?.dismiss()
            })
        }
    }

    private fun showShopLocationDialog(res: GetShopResponse.GetShopResponseData) {
        context?.let {
            val latLng = LatLng((res.geo_location?.coordinates?.get(0) ?: "0").toDouble(),
                    (res.geo_location?.coordinates?.get(1) ?: "0").toDouble())
            marketShopLocationDialog = MarketShopLocationDialog(it, latLng, res.shop_name
                    ?: "Shop Name")
            marketShopLocationDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            marketShopLocationDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            marketShopLocationDialog?.show()
        }
    }

    override fun updateBestSeller(list: ArrayList<GetProductResponse.GetProductResponseData>) {
        adapterTypeList.setBestSeller(list)
        adapterTypeList.notifyDataSetChanged()
    }

    override fun updatePopular(list: ArrayList<GetProductResponse.GetProductResponseData>) {
        adapterTypeList.setPopular(list)
        adapterTypeList.notifyDataSetChanged()
    }

    override fun updateNewProduct(list: ArrayList<GetProductResponse.GetProductResponseData>) {
        adapterTypeList.setNewProduct(list)
        adapterTypeList.notifyDataSetChanged()
    }

    fun checkBack() {
        if (stackPage.isNotEmpty()) {
            if (stackPage[stackPage.size - 1] == page_recc) {
                activity?.apply {
                    finish()
                }
            } else {
                edit_search.setText("")
                stackPage.removeAt(stackPage.size - 1)
                val lastPage = stackPage[stackPage.size - 1]
                hideAll()
                when (lastPage) {
                    page_recc -> {
                        shop_type = SHOP_LIST
                        all_list.visibility = View.VISIBLE
                        recycler_type_list.visibility = View.VISIBLE
                    }
                    pageShopList -> {
                        all_list.visibility = View.VISIBLE
                        layout_search.visibility = View.VISIBLE
                        layout_shop_show_type.visibility = View.VISIBLE
                        when (shop_type) {
                            SHOP_MAP -> {
                                map_search.visibility = View.VISIBLE
                            }
                            else -> {
                                recycler_search.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        } else {
            activity?.apply {
                finish()
            }
        }
    }

    private fun showTextDialog(message: String) {
        context?.let {
            marketTextDialog = MarketTextDialog(it)
            marketTextDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            marketTextDialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            marketTextDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            marketTextDialog?.show()

            marketTextDialog?.getView()?.let { view ->
                view.dl_text.text = message
            }
        }
    }

    private fun isGuest(): Boolean {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        return user.authority_info?.IsFb == "2"
    }

    override fun getCateSuccess(list: ArrayList<CateListResponse>) {
        context?.let {
            list.add(0, CateListResponse().apply {
                pd_online_cat_name = "ทุกหมวดหมู่สินค้า"
                pd_online_cat_id = ""
            })
            cate_adapter = CateArrayAdapter(it, R.layout.simple_list_item_2, list)
            list_cate.adapter = cate_adapter
            cate_adapter.notifyDataSetChanged()

            val listener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(p0: AdapterView<*>?) {

                }

                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                    search(edit_search.text.toString())
                }
            }

            list_cate.onItemSelectedListener = listener
        }
    }

    private fun sendOrder(product_id: String) {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {}

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {}
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val city_id = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestSendOrder(callbacks, SendOrderRequest(citizen_id, product_id, city_id))
    }

    override fun onStart() {
        super.onStart()
        map_search?.onStart()
    }

    override fun onResume() {
        super.onResume()
        map_search?.onResume()
    }

    override fun onPause() {
        super.onPause()
        map_search?.onPause()
    }

    override fun onStop() {
        super.onStop()
        map_search?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        map_search?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        map_search?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map_search?.onDestroy()
    }

    private fun showPopup() {
        context?.let {
            val customDialog = CustomDialog(it)
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()
            customDialog.setTitle(it.resources.getString(R.string.appplication_name))
            customDialog.setMessage(it.resources.getString(R.string.need_login_fav_text))
            customDialog.setOnClickOKListener(View.OnClickListener {
                customDialog.dismiss()
            })

            customDialog.b?.let { view ->
                view.dl_cancel.visibility = View.GONE
                view.dl_center.visibility = View.GONE
            }

            customDialog.setOnClickCancelListener(View.OnClickListener { customDialog.dismiss() })
        }
    }
}
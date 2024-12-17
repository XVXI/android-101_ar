package com.transcode.smartcity101p2.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.*
import android.widget.LinearLayout
import android.widget.Toast
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.*
import com.transcode.smartcity101p2.adapter.PhotosAdapter
import com.transcode.smartcity101p2.adapter.WishListAdapter
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.MarketWishListActivityContract
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.dialog.ProductDialog
import com.transcode.smartcity101p2.extension.concurrencyFormat
import com.transcode.smartcity101p2.extension.convertProductImage
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.CommonResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.SendOrderRequest
import com.transcode.smartcity101p2.model.buyapi.response.GetProductResponse
import com.transcode.smartcity101p2.presenter.MarketWishListActivityPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_wishlist.*
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.dialog_product.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MarketWishListFragment : CoreFragment(), WishListAdapter.ClickItemListener, MarketWishListActivityContract.View {
    companion object {
        fun newInstance(mode: Int, title: String): MarketWishListFragment {
            return MarketWishListFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var adapter: WishListAdapter
    lateinit var presenter: MarketWishListActivityPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.activity_wishlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle("")
        appBar.textTitle2.text = "ที่ถูกใจ"
        val img = context?.let {
            ContextCompat.getDrawable(it, R.mipmap.market_icon_fav_header)
        } ?: kotlin.run {
            null
        }
        img?.setBounds(0, 0, resources.getDimensionPixelSize(R.dimen.dp30), resources.getDimensionPixelSize(R.dimen.dp30))
        appBar.textTitle2.setCompoundDrawables(null, null, img, null)
        appBar.setHeaderColor(resources.getColor(R.color.purple))
        appBar.leftBt.visibility = View.INVISIBLE

        presenter = MarketWishListActivityPresenter(this)

        updateData()
    }

    fun updateData() {
        context?.let {
            adapter = WishListAdapter(it)
            adapter.setRecyclerView(recycler_wishlist)

            adapter.setClickListener(this)

            adapter.setData(getWishList())
            adapter.notifyDataSetChanged()
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

    override fun onClickDetail(res: GetProductResponse.GetProductResponseData) {
        val ctx = context
        if (ctx == null) {
            return
        }
        sendOrder(res.product_id ?: "")
        val productDialog = ProductDialog(ctx)
        productDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        productDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        productDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        productDialog.show()

        presenter.getProductDetail(res.product_id ?: "")

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

            val photoAdapter = PhotosAdapter(ctx)
            view.photos_viewpager.adapter = photoAdapter
            view.tab_layout.setupWithViewPager(view.photos_viewpager, true)
            photoAdapter.setData(imagelist)
            photoAdapter.notifyDataSetChanged()

            for (i in 0 until photoAdapter.count step 1) {
                val tab = LayoutInflater.from(ctx).inflate(R.layout.custom_tab2, null)
                tab.setBackgroundResource(R.drawable.tab_selector)
                view.tab_layout.getTabAt(i)?.customView = tab
                view.tab_layout.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.dimen_tab_width), LinearLayout.LayoutParams.MATCH_PARENT)
            }

            var shop_url = ""
            if (!res.Shop.ShopOwner?.shop_owner_img.isNullOrEmpty()) {
                shop_url = res.Shop.ShopOwner?.shop_owner_img.toString()
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

            view.dl_shop_name.text = res.shop_name
            view.dl_shop_detail.setOnClickListener {
                productDialog.dismiss()
//                val intent = Intent()
//                intent.putExtra("shop_id", res.shop_id)
//                setResult(Activity.RESULT_OK, intent)
//                finish()
                if (activity is MarketHomeActivity) {
                    (activity as MarketHomeActivity).showShop(res.shop_id ?: "")
                }
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

        }

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
            productDialog.dismiss()
        })
        productDialog.setOnClickCancelListener(View.OnClickListener {
            productDialog.dismiss()
        })

        productDialog.setOnDismissListener {
            if (getWishList().size != adapter.list.size) {
                adapter.setData(getWishList())
                adapter.notifyDataSetChanged()
            }

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

    override fun onClickAddcart(res: GetProductResponse.GetProductResponseData) {

    }

    override fun onClickDelete(res: GetProductResponse.GetProductResponseData) {
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
            }
        } else {
            wishList.add(res)
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val CitizenId = loginResponse.authority_info?.CitizenId ?: ""
        Hawk.delete(Const.WISHLIST + CitizenId)
        Hawk.put(Const.WISHLIST + CitizenId, wishList)

        adapter.setData(getWishList())
        adapter.notifyDataSetChanged()
    }

    private fun isGuest(): Boolean {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        return user.authority_info?.IsFb == "2"
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
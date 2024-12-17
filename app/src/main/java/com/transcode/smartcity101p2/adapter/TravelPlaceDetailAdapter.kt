package com.transcode.smartcity101p2.adapter

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.orhanobut.hawk.Hawk
import kotlinx.android.synthetic.main.item_place_detail_button.view.*
import kotlinx.android.synthetic.main.item_place_detail_comment.view.*
import kotlinx.android.synthetic.main.item_place_detail_desc.view.*
import kotlinx.android.synthetic.main.item_place_detail_store.view.*
import kotlinx.android.synthetic.main.item_place_detail_tel.view.*
import android.location.Location
import com.mapbox.mapboxsdk.geometry.LatLng
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.item_place_detail_landmark.view.*
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.TextView
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelPlaceDetailAdapterContract
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.extension.convertDate
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.model.travel.response.FavPlaceListResponse
import com.transcode.smartcity101p2.presenter.TravelPlaceDetailAdapterPresenter
import kotlinx.android.synthetic.main.custom_dialog.view.*
import kotlinx.android.synthetic.main.item_place_detail_ar.view.*
import kotlinx.android.synthetic.main.item_place_rate.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat


class TravelPlaceDetailAdapter(var context: Context) : AppBaseAdapter(), TravelPlaceDetailAdapterContract.View {
    override fun updateFavPlace(data: ArrayList<FavPlaceListResponse.FavPlaceListResponseData>) {
        favPlaceList = data
        notifyDataSetChanged()
    }

    override fun updateAddDelete() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.getFavPlaceList(user.authority_info?.CitizenId ?: "0")
    }

    var list = arrayListOf<Any>()
    var mystamp = arrayListOf<MyStampResponse.MyStampResponseData>()
    var detail = PlaceDetailsResponse.PlaceDetailsResponseData()
    var currentLatLng: LatLng? = null
    private var listener: ClickItem? = null
    private var favListener: ClickFavItem? = null
    private var commentListener: ClickCommentButtonListener? = null
    private var clickCheckinListener: ClickCheckinListener? = null

    var presenter = TravelPlaceDetailAdapterPresenter(this)
    var favPlaceList = arrayListOf<FavPlaceListResponse.FavPlaceListResponseData>()

    companion object {
        val TYPE_DESC = 0
        val TYPE_STORE = 1
        val TYPE_TEL = 2
        val TYPE_LANDMARK = 3
        val TYPE_BUTTON = 4
        val TYPE_COMMENT = 5
        val TYPE_RATE = 6
        val TYPE_AR = 7
    }

    fun setDefLatLng(latlng: LatLng) {
        currentLatLng = latlng

        val dist = FloatArray(1)

        val df1 = DecimalFormat("##.######")
        val df2 = DecimalFormat("###.######")
        val elat = df1.format((detail.lat ?: "0").toDouble())
        val elng = df2.format((detail.lng ?: "0").toDouble())

        val clat = df1.format(currentLatLng?.latitude ?: 16.054158)
        val clng = df2.format(currentLatLng?.longitude ?: 103.652355)

        Log.e("lat 1", elat)
        Log.e("lng 1", elng)
        Log.e("lat 2", clat)
        Log.e("lng 2", clng)

        Location.distanceBetween(elat.toDouble(), elng.toDouble(), clat.toDouble(), clng.toDouble(), dist)

        if (dist[0] / 500 <= 1) {
            //check in
            val place_id = detail.place_id ?: ""
            if (place_id.isNotEmpty()) {
                val checkin_time = Hawk.get((detail.place_id ?: "") + "_CK_TIME", 0L)
                if (System.currentTimeMillis() - checkin_time > 3600000f) {
                    Hawk.put(place_id + "_CK_TIME", System.currentTimeMillis())
                    checkIn()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val lp = ViewGroup.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val inflater = LayoutInflater.from(context)
        val view = when (viewType) {
            TYPE_DESC -> inflater.inflate(R.layout.item_place_detail_desc, parent, false)
            TYPE_STORE -> inflater.inflate(R.layout.item_place_detail_store, parent, false)
            TYPE_TEL -> inflater.inflate(R.layout.item_place_detail_tel, parent, false)
            TYPE_LANDMARK -> inflater.inflate(R.layout.item_place_detail_landmark, parent, false)
            TYPE_BUTTON -> inflater.inflate(R.layout.item_place_detail_button, parent, false)
            TYPE_RATE -> inflater.inflate(R.layout.item_place_rate, parent, false)
            TYPE_AR -> inflater.inflate(R.layout.item_place_detail_ar, parent, false)
            else -> inflater.inflate(R.layout.item_place_detail_comment, parent, false)
        }
        val viewHolder = ViewHolder(view)
        view.layoutParams = lp
        return viewHolder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val v = holder.itemView
        when (getItemViewType(position)) {
            TYPE_DESC -> {
                v.text_place_name.text = if (detail.place_name.isNullOrEmpty()) {
                    "-"
                } else {
                    detail.place_name
                }
                val textRemark = if (detail.remark.isNullOrEmpty() || detail.remark.toString() == "null") {
                    context.resources.getString(R.string.coomon_text_details) + " : " + "-"
                } else {
                    context.resources.getString(R.string.coomon_text_details) + " : " + detail.remark
                }
                v.text_place_desc.text = textRemark

                if (detail.is_suggest == "1") {
                    v.image_has_stamp.visibility = View.VISIBLE
                } else {
                    v.image_has_stamp.visibility = View.GONE
                }

                var isFav = false

                for (fav in favPlaceList) {
                    if (detail.place_id == fav.place_id) {
                        isFav = true
                        break
                    }
                }

                if (isFav) {
                    v.text_fav.load("", R.mipmap.icon_fav_active)
                } else {
                    v.text_fav.load("", R.mipmap.icon_fav_inactive)
                }

                v.text_fav.setOnClickListener {

                    if (isGuest()) {
                        showPopup()
                        return@setOnClickListener
                    }

                    if (isFav) {
                        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                        presenter.deleteFavPlace(user.authority_info?.CitizenId
                                ?: "0", detail.place_id ?: "0")
                    } else {
                        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                        presenter.addFavPlace(user.authority_info?.CitizenId
                                ?: "0", detail.place_id ?: "0")
                    }
                }

                v.image_location.setOnClickListener {
                    val gmmIntentUri = Uri.parse("geo:" + detail.lat + "," + detail.lng + "?q=" + detail.lat + "," + detail.lng + "(" + detail.place_name + ")")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(mapIntent)
                    }
                }
            }
            TYPE_STORE -> {
                v.layout_mon.visibility = View.GONE
                v.layout_tue.visibility = View.GONE
                v.layout_wed.visibility = View.GONE
                v.layout_thu.visibility = View.GONE
                v.layout_fri.visibility = View.GONE
                v.layout_sat.visibility = View.GONE
                v.layout_sun.visibility = View.GONE

                var mon = PlaceDetailsResponse.StoreOpens()
                mon.StoreopenId = "0"
                var tue = PlaceDetailsResponse.StoreOpens()
                tue.StoreopenId = "0"
                var wed = PlaceDetailsResponse.StoreOpens()
                wed.StoreopenId = "0"
                var thu = PlaceDetailsResponse.StoreOpens()
                thu.StoreopenId = "0"
                var fri = PlaceDetailsResponse.StoreOpens()
                fri.StoreopenId = "0"
                var sat = PlaceDetailsResponse.StoreOpens()
                sat.StoreopenId = "0"
                var sun = PlaceDetailsResponse.StoreOpens()
                sun.StoreopenId = "0"

                for (store in detail.store_open) {
                    //1 = sun , 7 = sat
                    try {
                        when (store.Daytype) {
                            "2" -> {
                                if ((store.StoreopenId ?: "0").toInt() > (mon.StoreopenId
                                                ?: "0").toInt()) {
                                    mon = store
                                }
                            }
                            "3" -> {
                                if ((store.StoreopenId ?: "0").toInt() > (tue.StoreopenId
                                                ?: "0").toInt()) {
                                    tue = store
                                }
                            }
                            "4" -> {
                                if ((store.StoreopenId ?: "0").toInt() > (wed.StoreopenId
                                                ?: "0").toInt()) {
                                    wed = store
                                }
                            }
                            "5" -> {
                                if ((store.StoreopenId ?: "0").toInt() > (thu.StoreopenId
                                                ?: "0").toInt()) {
                                    thu = store
                                }
                            }
                            "6" -> {
                                if ((store.StoreopenId ?: "0").toInt() > (fri.StoreopenId
                                                ?: "0").toInt()) {
                                    fri = store
                                }
                            }
                            "7" -> {
                                if ((store.StoreopenId ?: "0").toInt() > (sat.StoreopenId
                                                ?: "0").toInt()) {
                                    sat = store
                                }
                            }
                            "1" -> {
                                if ((store.StoreopenId ?: "0").toInt() > (sun.StoreopenId
                                                ?: "0").toInt()) {
                                    sun = store
                                }
                            }
                            else -> {

                            }
                        }
                    } catch (e: Exception) {
                    }
                }

                updateDateTimeLayout(v.layout_mon, v.mon_open, mon)
                updateDateTimeLayout(v.layout_tue, v.tue_open, tue)
                updateDateTimeLayout(v.layout_wed, v.wed_open, wed)
                updateDateTimeLayout(v.layout_thu, v.thu_open, thu)
                updateDateTimeLayout(v.layout_fri, v.fri_open, fri)
                updateDateTimeLayout(v.layout_sat, v.sat_open, sat)
                updateDateTimeLayout(v.layout_sun, v.sun_open, sun)
            }
            TYPE_TEL -> {
                val textTel = if (detail.contract_tel.isNullOrEmpty() || detail.contract_tel.toString() == "null") {
                    context.resources.getString(R.string.callphone_dialog_header) + " -"
                } else {
                    var s = detail.contract_tel ?: ""
                    if (s.length > 4) {
                        if (s.startsWith("02")) {
                            s = s.replace("02", "02-")
                        } else {
                            s = s.substring(0, 3) + "-" + s.substring(3, s.length)
                        }
                    }
                    context.resources.getString(R.string.callphone_dialog_header) + " " + s
                }
                v.text_telno.text = textTel

                if (!detail.contract_tel.isNullOrEmpty()) {
                    v.text_telno.setOnClickListener {
                        val phone = detail.contract_tel
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                        context.startActivity(intent)
                    }
                } else {
                    v.text_telno.setOnClickListener { }
                }
            }
            TYPE_LANDMARK -> {
                var isCheckin = false

                for (i in mystamp) {
                    if (detail.place_id == i.place_id) {
                        isCheckin = true
                        break
                    }
                }

                v.landmark_layout_outofrange.visibility = View.GONE
                v.landmark_layout_checked.visibility = View.GONE
                v.landmark_layout_checkin.visibility = View.GONE

                if (isCheckin) {
                    v.landmark_layout_outofrange.visibility = View.GONE
                    v.landmark_layout_checked.visibility = View.VISIBLE
                    v.landmark_layout_checkin.visibility = View.GONE

                    val imageUrl = if (detail.stamp_img.isNullOrEmpty()) {
                        ""
                    } else {
                        AppUtils.getImageUrl(detail.stamp_img.toString())
                    }

                    v.landmark_image_checked.load(imageUrl, R.mipmap.out_range)
                } else {
                    currentLatLng?.let {
                        val dist = FloatArray(1)

                        val df1 = DecimalFormat("##.######")
                        val df2 = DecimalFormat("###.######")
                        val elat = df1.format((detail.lat ?: "0").toDouble())
                        val elng = df2.format((detail.lng ?: "0").toDouble())

                        val clat = df1.format(it.latitude)
                        val clng = df2.format(it.longitude)

                        Location.distanceBetween(elat.toDouble(), elng.toDouble(), clat.toDouble(), clng.toDouble(), dist)

                        if (dist[0] / 500 > 1) {
                            //here your code or alert box for outside 500m radius area
                            v.landmark_layout_outofrange.visibility = View.VISIBLE
                            v.landmark_layout_checked.visibility = View.GONE
                            v.landmark_layout_checkin.visibility = View.GONE
                        } else {
                            v.landmark_layout_outofrange.visibility = View.GONE
                            v.landmark_layout_checked.visibility = View.GONE
                            v.landmark_layout_checkin.visibility = View.VISIBLE

                            val imageUrl = if (detail.stamp_img_bw.isNullOrEmpty()) {
                                ""
                            } else {
                                AppUtils.getImageUrl(detail.stamp_img_bw.toString())
                            }

                            v.landmark_image_checkin.load(imageUrl, R.mipmap.out_range)

                            v.landmark_layout_checkin.setOnClickListener {
                                clickCheckinListener?.onClickCheckin(detail.place_id ?: "")
                            }
                        }
                    }
                }
            }
            TYPE_BUTTON -> {
                v.button_comment.setOnClickListener {
                    commentListener?.onClickCommentButton()
                }
            }
            TYPE_COMMENT -> {
                val data = list[position] as PlaceDetailsResponse.Comment
                v.comment_name.text = data.fname + " " + data.lname
                v.comment_date.text = data.create_datetime.toString().convertDate(25200000)
                v.comment_message.text = data.comment
                v.ratings.text = data.rating

                val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                val type = when {
                    login.authority_info?.IsFb == "1" -> "facebook"
                    login.authority_info?.IsFb == "2" -> "guest"
                    login.authority_info?.IsFb == "3" -> "google"
                    else -> "email"
                }

                if (login.authority_info?.CitizenId == data.citizen_id) {
                    when (type) {
                        "email" -> {
                            val image = Hawk.get<String>("user_image") ?: ""
                            if (image != "" || image != "null") {
                                v.comment_icon.load(image, R.mipmap.man)
                            }
                        }
                        "facebook" -> if (fb_image.isNullOrEmpty()) {
                            val accessToken = AccessToken.getCurrentAccessToken()
                            val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                                // Application code
                                fb_image = try {
                                    json.getJSONObject("picture").getJSONObject("data").getString("url")
                                } catch (exception: Exception) {
                                    ""
                                }
                                v.comment_icon.load(fb_image, R.mipmap.man)
                            }
                            val parameters = Bundle()
                            parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)")
                            request.parameters = parameters
                            request.executeAsync()
                        } else {
                            v.comment_icon.load(fb_image, R.mipmap.man)
                        }
                        "google" -> {
                            val acct = Hawk.get<GAccount>("G_ACCOUNT")
                            if (acct != null) {
                                v.comment_icon.load(acct.photoUrl, R.mipmap.man)
                            } else {
                                v.comment_icon.load("", R.mipmap.man)
                            }
                        }
                    }
                } else {
                    v.comment_icon.load(AppUtils.getImageUrl(data.citizen_id), R.mipmap.man)
                }
            }
            TYPE_RATE -> {
                v.ratingText.text = detail.rating
            }
            TYPE_AR -> {
                v.text_ar.text = context.resources.getString(R.string.text_ar_view) + " : " + (detail.AR_total.toString())
            }
        }
    }

    var fb_image = ""
    private fun checkIn() {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                Log.e("success", "success")
            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                Log.e("error", "error")
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val city_id = loginResponse.authority_info?.CityId ?: ""
        val place_id = detail.place_id ?: ""
        ApiRequest.INSTANCE.requestInVisit(callbacks, InVisitRequest(citizen_id, place_id, city_id))
    }

    fun setRecyclerView(rv: RecyclerView) {
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(rv.context, LinearLayoutManager.VERTICAL, false)
        rv.adapter = this
    }

    fun setListData(list: ArrayList<Any>) {
        this.list = list

        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.getFavPlaceList(user.authority_info?.CitizenId ?: "0")
    }

    fun setMyStamp(list: ArrayList<MyStampResponse.MyStampResponseData>) {
        this.mystamp = list
    }

    fun setData(detail: PlaceDetailsResponse.PlaceDetailsResponseData) {
        this.detail = detail
    }

    fun notifyDataAndFav() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.getFavPlaceList(user.authority_info?.CitizenId ?: "0")
    }

    fun setClickListener(listener: ClickItem) {
        this.listener = listener
    }

    fun setClickFavListener(listener: ClickFavItem) {
        this.favListener = listener
    }

    fun setClickCommentListener(listener: ClickCommentButtonListener) {
        this.commentListener = listener
    }

    fun setClickCheckinListener(listener: ClickCheckinListener) {
        this.clickCheckinListener = listener
    }

    interface ClickItem {
        fun onclickItem(res: Any)
    }

    interface ClickCommentButtonListener {
        fun onClickCommentButton()
    }

    interface ClickFavItem {
        fun onClickFavItem()
    }

    interface ClickCheckinListener {
        fun onClickCheckin(place_id: String)
    }

    override fun getItemViewType(position: Int): Int {
        if (list[position] is String) {
            when (list[position]) {
                "desc" -> {
                    return TYPE_DESC
                }
                "opens" -> {
                    return TYPE_STORE
                }
                "tel" -> {
                    return TYPE_TEL
                }
                "landmark" -> {
                    return TYPE_LANDMARK
                }
                "button" -> {
                    return TYPE_BUTTON
                }
                "rate" -> {
                    return TYPE_RATE
                }
                "ar" -> {
                    return TYPE_AR
                }
                else -> {
                    return TYPE_COMMENT
                }
            }
        } else if (list[position] is PlaceDetailsResponse.Comment) {
            return TYPE_COMMENT
        } else {
            return TYPE_COMMENT
        }
    }

    private fun favIt(place_id: String) {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                Log.e("success", "success")
            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                Log.e("error", "error")
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        ApiRequest.INSTANCE.requestPlaceFav(callbacks, PlaceFavRequest(place_id, citizen_id))
    }

    private fun isGuest(): Boolean {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        return user.authority_info?.IsFb == "2"
    }

    private fun showPopup() {
        val customDialog = CustomDialog(context)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        customDialog.show()
        customDialog.setTitle(context.resources.getString(R.string.appplication_name))
        customDialog.setMessage(context.resources.getString(R.string.need_login_fav_text))
        customDialog.setOnClickOKListener(View.OnClickListener {
            customDialog.dismiss()
        })

        customDialog.b?.let { view ->
            view.dl_cancel.visibility = View.GONE
            view.dl_center.visibility = View.GONE
        }

        customDialog.setOnClickCancelListener(View.OnClickListener { customDialog.dismiss() })
    }

    private fun convertDatetime(DateTime: String?): String {
        return if (DateTime.isNullOrEmpty()) {
            ""
        } else {
            val vs = DateTime ?: ""
            if (vs.length > 5) {
                vs.substring(0, vs.length - 3)
            } else {
                vs
            }
        }
    }

    fun updateDateTimeLayout(layout: View, day: TextView, store: PlaceDetailsResponse.StoreOpens) {
        layout.visibility = View.VISIBLE
        day.text = "${convertDatetime(store.OpenTime)} - ${convertDatetime(store.CloseTime)}"
    }
}
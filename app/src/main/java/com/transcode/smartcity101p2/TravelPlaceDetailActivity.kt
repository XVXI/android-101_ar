package com.transcode.smartcity101p2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import com.mapbox.mapboxsdk.geometry.LatLng
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.adapter.PhotosAdapter
import com.transcode.smartcity101p2.adapter.TravelPlaceDetailAdapter
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelPlaceDetailActivityContract
import com.transcode.smartcity101p2.dialog.CommentDialog
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.presenter.TravelPlaceDetailActivityPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_travel_place_detail.*
import kotlinx.android.synthetic.main.appbar_main2.appbar
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.comment_dialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelPlaceDetailActivity : CoreActivity(), TravelPlaceDetailActivityContract.View, TravelPlaceDetailAdapter.ClickCommentButtonListener, TravelPlaceDetailAdapter.ClickCheckinListener {
    override fun updateCheckin(place_id: String) {
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.getMyStamp(login.authority_info?.CitizenId ?: "", place_id)
    }

    override fun onClickCheckin(place_id: String) {
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.addCheckin(place_id, login.authority_info?.CitizenId ?: "")
    }

    override fun updateMyStamp(list: ArrayList<MyStampResponse.MyStampResponseData>, place_id: String) {
        adapter.setMyStamp(list)
        presenter.getPlaceByID(place_id)
    }

    override fun updateComment(success: Boolean) {
        if (success) {
            autoScroll = true
            val place_id = intent?.extras?.getString("place_id") ?: ""
            val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            presenter.getMyStamp(login.authority_info?.CitizenId ?: "", place_id)
        } else {
            Toast.makeText(this, Const.MESSAGE_ERROR, Toast.LENGTH_LONG).show()
        }
    }

    override fun onClickCommentButton() {
        val commentDialog = CommentDialog(this)
        commentDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        commentDialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
        commentDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        commentDialog.show()
//        commentDialog.hideRating()
        commentDialog.setOnClickOKListener(View.OnClickListener {
            val view = commentDialog.getView()
            val place_id = intent?.extras?.getString("place_id") ?: ""
            val res = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            res?.let {
                if (!view?.dl_message?.text.isNullOrEmpty()) {
                    presenter.addComment(it.authority_info?.CitizenId ?: "",
                            place_id,
                            view?.dl_message?.text.toString(),
                            (commentDialog.getView()?.ratingBar?.rating ?: 0).toString())
                }
            }
            commentDialog.dismiss()
        })
        commentDialog.setOnClickCancelListener(View.OnClickListener {
            commentDialog.dismiss()
        })
    }

    override fun updateView(data: PlaceDetailsResponse.PlaceDetailsResponseData) {

        updateImage(data.img)

        dataList.clear()
        dataList.add("desc")

        data.store_open?.apply {
            if (this.size > 0) {
                dataList.add("opens")
            }
        }

        dataList.add("tel")
        dataList.add("rate")

        if (data.is_suggest == "1") {
            dataList.add("landmark")
        }

        if (data.is_ar.toString() == "1") {
            dataList.add("ar")
        }

        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val isFB = user.authority_info?.IsFb
        if (isFB != "2") {
            dataList.add("button")
        }

        for (d in data.comment) {
            dataList.add(d)
        }

        adapter.setListData(dataList)
        adapter.setData(data)

        adapter.notifyDataAndFav()

        if (autoScroll) {
            autoScroll = false
            recyclerView.post { recyclerView.scrollToPosition(adapter.itemCount - 1) }
        }
    }

    var dataList = arrayListOf<Any>()
    var autoScroll = false
    private var locationManager: LocationManager? = null

    lateinit var presenter: TravelPlaceDetailActivityPresenter

    lateinit var adapter: TravelPlaceDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_place_detail)

        presenter = TravelPlaceDetailActivityPresenter(this)

        adapter = TravelPlaceDetailAdapter(this)
        adapter.setRecyclerView(recyclerView)
        adapter.setClickCommentListener(this)
        adapter.setClickCheckinListener(this)

        if (isGrantedPermission()) {
            val location = getCurrentLocation()
            adapter.setDefLatLng(LatLng(location?.latitude?:16.054158, location?.longitude?:103.6501564))
        } else {
            val location = LatLng(16.054158, 103.652355)
            adapter.setDefLatLng(LatLng(location.latitude, location.longitude))
        }

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitleColor(resources.getColor(R.color.dark_purple))
        appBar.leftBt.setOnClickListener {
            finish()
        }

        intent?.extras?.let {
            val title = it.getString("title") ?: ""
            val place_id = it.getString("place_id") ?: ""
            appBar.setTitle(title)
            val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            presenter.getMyStamp(login.authority_info?.CitizenId ?: "", place_id)
            sendView(place_id)
        }

    }

    fun isGrantedPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location? {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var gps_enabled = false

        try {
            gps_enabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        } catch (ex: Exception) {
        }

        if (!gps_enabled) {
            Toast.makeText(this, "please enable gps", Toast.LENGTH_LONG).show()
        }

//        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10.0f, locationListener)
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
//        locationManager?.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)
        locationManager?.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10.0f, locationListener)

        return locationManager?.getLastKnownLocation(locationManager?.getBestProvider(criteria, false))
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location?) {
            val latLng = LatLng(p0?.latitude ?: 16.054158, p0?.longitude ?: 103.6501564)
            adapter.setDefLatLng(latLng)
            adapter.notifyDataAndFav()
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }
    }

    override fun onDestroy() {
        intent?.extras?.let {
            val plan_data = it.getString("plan_data") ?: ""
            if (Hawk.get<Boolean>("by_noti") != true) {
                if (plan_data.isNotEmpty()) {
                    val intent = Intent(this, TravelPlanDetailActivity::class.java)
                    intent.putExtra("data", plan_data)
                    startActivity(intent)
                }
            }
        }

        Hawk.put("by_noti", false)
        super.onDestroy()
        locationManager?.removeUpdates(locationListener)
    }

    fun updateImage(imageList: ArrayList<PlaceDetailsResponse.Img>) {
        val photoAdapter = PhotosAdapter(this)
        photos_viewpager.adapter = photoAdapter
        val list = arrayListOf<String>()
        for (n in imageList) {
            when {
                n.image_path.isNullOrEmpty() -> list.add("")
                (n.image_path ?: "").startsWith("http") -> list.add(n.image_path ?: "")
                else -> list.add(AppUtils.getImageUrl(n.image_path ?: ""))
            }

        }

        if (list.size <= 0) {
            frame_image.visibility = View.GONE
        }

        photoAdapter.setData(list)
        photoAdapter.notifyDataSetChanged()
        tab_layout.setupWithViewPager(photos_viewpager, true)
        photos_viewpager.setCurrentItem(0, false)

        for (i in 0 until photoAdapter.count step 1) {
            val tab = LayoutInflater.from(this).inflate(R.layout.custom_tab2, null)
            tab.setBackgroundResource(R.drawable.tab_selector)
            tab_layout.getTabAt(i)?.customView = tab
            tab_layout.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.dimen_tab_width), LinearLayout.LayoutParams.MATCH_PARENT)
        }
        photos_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position >= photoAdapter.count) {
                    photos_viewpager?.setCurrentItem(0, false)
                } else {
                    photos_viewpager?.setCurrentItem(position, true)
                }
            }

        })
    }

    private fun sendView(place_id: String) {
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
        ApiRequest.INSTANCE.requestSendView(callbacks, SendViewRequest(citizen_id, place_id, city_id))
    }
}
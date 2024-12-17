package com.transcode.smartcity101p2

import android.content.Context
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.Window
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.core.constants.Constants
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.annotations.*
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.adapter.TravelPlanListAdapter
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelPlanDetailActivityContract
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.model.travel.response.FavPlanListResponse
import com.transcode.smartcity101p2.presenter.TravelPlanDetailActivityPresenter
import com.transcode.smartcity101p2.utils.CustomFont
import com.transcode.smartcity101p2.utils.FontManager
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.activity_travel_plan_detail.*
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.custom_dialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TravelPlanDetailActivity : CoreActivity(), TravelPlanDetailActivityContract.View, OnMapReadyCallback, TravelPlanListAdapter.ClickItem {
    override fun updateAddDelete() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        presenter.getFavPlanList(user.authority_info?.CitizenId ?: "0")
    }

    override fun onclickItem(res: PlanDetailResponse.DataDetails) {
        val intent = Intent(this, TravelPlaceDetailActivity::class.java)
        intent.putExtra("title", res.PlaceName ?: "")
        intent.putExtra("place_id", res.PlaceId ?: "")
        data?.let {
            val gson = GsonBuilder().create()
            val jsonData = gson.toJson(it)
            intent.putExtra("plan_data", jsonData)
        }
        startActivity(intent)
        Hawk.put("by_noti", false)
        finish()
    }

    override fun updateFavPlan(fav_data: ArrayList<FavPlanListResponse.FavPlanListResponseData>) {
        isFav = false

        for (fav in fav_data) {
            if (data?.PlanId == fav.plan_id) {
                isFav = true
                break
            }
        }

        if (isFav) {
            text_fav.load("", R.mipmap.icon_fav_active)
        } else {
            text_fav.load("", R.mipmap.icon_fav_inactive)
        }
    }

    override fun onMapReady(map: MapboxMap) {
        map.setStyle(Style.Builder().fromUri(Const.MAP_STYLE_URI), Style.OnStyleLoaded {

        })

        mapboxMap = map
        map.infoWindowAdapter = MyInfoWindowAdapter()

        presenter.getPlanDetail(data?.PlanId ?: "")
    }

    private var mapboxMap: MapboxMap? = null
    lateinit var presenter: TravelPlanDetailActivityPresenter
    var data: PlanListResponse.PlanListResponseData? = null
    val listDetail = arrayListOf<Any>()
    val listAll = arrayListOf<PlanDetailResponse.DataDetails>()

    lateinit var adapter: TravelPlanListAdapter
    var isFav = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_travel_plan_detail)

        adapter = TravelPlanListAdapter(this)
        adapter.setRecyclerView(recyclerView)
        adapter.setClickListener(this)
        adapter.setData(arrayListOf())
        adapter.notifyDataSetChanged()

        intent?.extras?.let {
            val gson = Gson()
            data = gson.fromJson(it.getString("data"), PlanListResponse.PlanListResponseData::class.java)

            text_title.text = data?.PlanName
        }

        presenter = TravelPlanDetailActivityPresenter(this)

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(getString(R.string.travel_string_plan))
        appBar.setTitleColor(resources.getColor(R.color.dark_purple))
        appBar.leftBt.setOnClickListener {
            finish()
        }

        try {
            mapView.onCreate(savedInstanceState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapView.getMapAsync(this)

        text_fav.setOnClickListener {
            if (isGuest()) {
                showPopup()
                return@setOnClickListener
            }

            if (isFav) {
                //delete
                val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                presenter.deleteFavPlan(user.authority_info?.CitizenId ?: "0", data?.PlanId ?: "0")
            } else {
                //add
                val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
                presenter.addFavPlan(user.authority_info?.CitizenId ?: "0", data?.PlanId ?: "0")
            }
        }

        planView(data?.PlanId ?: "")

        updateAddDelete()
    }

    override fun updatePlanDetail(data: ArrayList<PlanDetailResponse.PlanDetailResponseData>) {
        listDetail.clear()
        if (data.isNotEmpty()) {
            val d = data[0]
            var currrent_day = ""
            listAll.addAll(d.Details)
            for (detail in d.Details) {
                if (currrent_day != detail.day_no) {
                    currrent_day = detail.day_no ?: ""
                    listDetail.add(detail.day_no ?: "")
                }
                listDetail.add(detail)
            }
            updateView()
        }

        adapter.setData(listDetail)
        adapter.notifyDataSetChanged()
    }

    private fun updateView() {
        var i = 1
        for (data in listDetail) {
            if (data is PlanDetailResponse.DataDetails) {
                setMarker(LatLng((data.Lat ?: "0").toDouble(), (data.Lng ?: "0").toDouble()),
                        data.PlaceName ?: "", i.toString())
                i += 1
            }
        }

        if (listAll.isNotEmpty()) {
            val center = listAll.size / 2
            val data = listAll[center]

            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                    LatLng((data.Lat ?: "0").toDouble(),
                            (data.Lng ?: "0").toDouble()),
                    12.0)
            mapboxMap?.moveCamera(cameraUpdate)
        }

        mapboxMap?.setOnInfoWindowClickListener {
            for (data in listDetail) {
                if (data is PlanDetailResponse.DataDetails) {
                    if (it.title == data.PlaceName) {
                        val intent = Intent(this, TravelPlaceDetailActivity::class.java)
                        intent.putExtra("title", data.PlaceName ?: "")
                        intent.putExtra("place_id", data.PlaceId ?: "")
                        startActivity(intent)
                        break
                    }
                }
            }
            true
        }

        if (listAll.size > 1) {
            for (i in 0 until listAll.size - 1) {
                getRoute(Point.fromLngLat((listAll[i].Lng ?: "0").toDouble(), (listAll[i].Lat
                        ?: "0").toDouble()),
                        Point.fromLngLat((listAll[i + 1].Lng
                                ?: "0").toDouble(), (listAll[i + 1].Lat ?: "0").toDouble()))
            }
        }
    }

    private fun getRoute(origin: Point, destination: Point) {
        NavigationRoute.builder(this)
                .accessToken(getString(R.string.mapbox_access_token))
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(object : Callback<DirectionsResponse> {
                    override fun onResponse(call: Call<DirectionsResponse>, response: Response<DirectionsResponse>) {
                        response.body()?.let {
                            drawNavigationPolylineRoute(it.routes()[0])
                        }
                    }

                    override fun onFailure(call: Call<DirectionsResponse>, t: Throwable) {

                    }
                })
    }

    fun drawNavigationPolylineRoute(route: DirectionsRoute) {
        if (mapboxMap != null) {
            route.geometry()?.let {
                val lineString = LineString.fromPolyline(it, Constants.PRECISION_6)
                val coordinates = lineString.coordinates()
                val points = arrayListOf<LatLng>()

                for (data in coordinates) {
                    points.add(LatLng(data.latitude(), data.longitude()))
                }

                val polylineOptions = PolylineOptions()
                        .addAll(points)
                        .color(resources.getColor(R.color.purple))
                        .width(2f)
                mapboxMap?.addPolyline(polylineOptions)
            }
        }
    }

    private fun setMarker(lngYou: LatLng, title: String, pos: String) {
        val marker = MarkerOptions()
                .title(title)
                .position(LatLng(lngYou.latitude, lngYou.longitude))
//                .icon(bitmapDescriptorFromVector(this, R.mipmap.icon_pin_101))
                .icon(IconFactory.getInstance(this).fromBitmap(writeTextOnDrawable(R.mipmap.icon_pin_101, pos)))
        mapboxMap?.let {
            it.addMarker(marker)
        }
    }

    private fun writeTextOnDrawable(drawableId: Int, text: String): Bitmap {
        val bitmap = BitmapFactory.decodeResource(resources, drawableId).copy(Bitmap.Config.ARGB_8888, true)
        val bm = Bitmap.createScaledBitmap(bitmap, 100, 120, false)
        val tf = FontManager.getTypeFace(this, CustomFont.KANIT_REGULAR)

        val paint = Paint()
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE
        paint.typeface = tf
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = convertToPixels(11)

        val textRect = Rect()
        paint.getTextBounds(text, 0, text.length, textRect)

        val canvas = Canvas(bm)
        val xPos = (canvas.width / 2) - 2
        val yPos = (canvas.height / 3) - ((paint.descent() + paint.ascent()) / 2)
        canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), paint)
        return bm
    }

    private fun convertToPixels(nDP: Int): Float {
        val conversionScale = resources.displayMetrics.density

        return ((nDP * conversionScale) + 0.5f).toFloat()

    }

    internal inner class MyInfoWindowAdapter : MapboxMap.InfoWindowAdapter {

        private val myContentsView: View = layoutInflater.inflate(R.layout.custom_infowindow, null)

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

    }


    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    private fun planView(plan_id: String) {
        val callbacks = object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>?, response: Response<CommonResponse>?) {
                Log.e("success", "Success")
            }

            override fun onFailure(call: Call<CommonResponse>?, t: Throwable?) {
                Log.e("error", "error")
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val citizen_id = loginResponse.authority_info?.CitizenId ?: ""
        val city_id = loginResponse.authority_info?.CityId ?: ""
        ApiRequest.INSTANCE.requestPlanView(callbacks, PlanViewRequest(citizen_id, plan_id, city_id))
    }

    private fun favIt(plan_id: String) {
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
        ApiRequest.INSTANCE.requestPlanFav(callbacks, PlanFavRequest(plan_id, citizen_id))
    }

    private fun isGuest(): Boolean {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        return user.authority_info?.IsFb == "2"
    }

    private fun showPopup() {
        val customDialog = CustomDialog(this)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        customDialog.show()
        customDialog.setTitle(getString(R.string.appplication_name))
        customDialog.setMessage(getString(R.string.need_login_fav_text))
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
package com.transcode.smartcity101p2.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.v4.app.ActivityCompat
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.content.ContextCompat
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.orhanobut.hawk.Hawk
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.ComplainChatListAdapter
import com.transcode.smartcity101p2.adapter.ImageListAdapter
import com.transcode.smartcity101p2.contract.ComplainDetailFragmentContract
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.complain.ComplainByIDResponse
import com.transcode.smartcity101p2.model.complain.ComplainListResponse
import com.transcode.smartcity101p2.presenter.ComplainDetailFragmentPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import kotlinx.android.synthetic.main.fragment_complain_detail.*
import kotlin.math.roundToInt

class ComplainDetailFragment : CoreFragment(), ComplainDetailFragmentContract.View, OnMapReadyCallback {
    override fun createDialogSuccess() {
        presenter.getDetailByID(listData.complain_id ?: "0")
    }

    override fun updateView(data: ComplainByIDResponse.ComplainByIDResponseData) {
        adapter.setData(data.dialog)
        adapter.notifyDataSetChanged()
        if (adapter.itemCount > 0) {
            chat_list?.post {
                chat_list?.scrollToPosition(adapter.itemCount - 1)
            }
        }

        if (data.img.size > 0) {
            val img = arrayListOf<String>()
            for (i in data.img) {
                img.add(i.img_url ?: "")
            }
            imageListAdapter.setData(img)
            imageListAdapter.notifyDataSetChanged()
        }

        if (incident_sliding_layout?.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            clearChat()
        }

        context?.let {
            val textcode = getString(R.string.common_text_complain_code) + " : " + when {
                data.complain_code.isNullOrEmpty() -> "-"
                data.complain_code == "null" -> "-"
                else -> data.complain_code.toString()
            }
            text_complain_code.text = textcode
        }
    }

    override fun onMapReady(map: MapboxMap) {
        mapboxMap = map
        context?.let {
            setUpMap(it)
        }

        val latLng = if (isGrantedPermission()) {
            getLocation()
        } else {
            LatLng(16.054158, 103.652355)
        }

        val newlatlng = LatLng(listData.complain_lat?.toDouble() ?: 16.054158,
                listData.complain_lng?.toDouble() ?: 103.6501564)

        setMarker(newlatlng, getString(R.string.complain_complain_event_location))

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(newlatlng, 12.0)
        map?.moveCamera(cameraUpdate)
        map?.infoWindowAdapter = MyInfoWindowAdapter()
    }

    private fun setUpMap(context: Context) {
        mapboxMap?.setStyle(Style.Builder().fromUri(Const.MAP_STYLE_URI), Style.OnStyleLoaded {

        })
        if (ActivityCompat.checkSelfPermission(context,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context as Activity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 100)
            return
        }
    }

    companion object {
        fun newInstance(data: ComplainListResponse.ComplainListResponseData, open_chat: Boolean): ComplainDetailFragment {
            return ComplainDetailFragment().apply {
                val gson = GsonBuilder().create()
                val gsonData = gson.toJson(data)
                val bundle = Bundle()
                bundle.putString("data", gsonData)
                bundle.putBoolean("open_chat", open_chat)
                arguments = bundle
            }
        }
    }

    private var mapboxMap: MapboxMap? = null
    private var locationManager: LocationManager? = null
    private var savedInstanceStates: Bundle? = null
    lateinit var listData: ComplainListResponse.ComplainListResponseData
    lateinit var presenter: ComplainDetailFragmentPresenter
    lateinit var adapter: ComplainChatListAdapter
    lateinit var imageListAdapter: ImageListAdapter
    var open_chat = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.savedInstanceStates = savedInstanceState
        return inflater.inflate(R.layout.fragment_complain_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments
        bundle?.let {
            if (it.containsKey("data")) {
                val gson = Gson()
                listData = gson.fromJson(it.getString("data"), ComplainListResponse.ComplainListResponseData::class.java)
            }

            if (it.containsKey("open_chat")) {
                open_chat = it.getBoolean("open_chat")
            }
        }
        initView()
    }

    private fun initView() {
        button_back.setOnClickListener {
            activity?.apply {
                finish()
            }
        }

        when (listData.complain_type_id) {
            "1" -> {
                image_type.load("", R.mipmap.icon_complain_1)
                text_type.text = getString(R.string.complain_complain_type_1)
            }
            "2" -> {
                image_type.load("", R.mipmap.icon_complain_2)
                text_type.text = getString(R.string.complain_complain_type_2)
            }
            "3" -> {
                image_type.load("", R.mipmap.icon_complain_3)
                text_type.text = getString(R.string.complain_complain_type_3)
            }
            "4" -> {
                image_type.load("", R.mipmap.icon_complain_4)
                text_type.text = getString(R.string.complain_complain_type_4)
            }
            "5" -> {
                image_type.load("", R.mipmap.icon_complain_5)
                text_type.text = getString(R.string.complain_complain_type_5)
            }
            "6" -> {
                image_type.load("", R.mipmap.icon_complain_6)
                text_type.text = getString(R.string.complain_complain_type_6)
            }
            "7" -> {
                image_type.load("", R.mipmap.icon_complain_7)
                text_type.text = getString(R.string.complain_complain_type_7)
            }
            "8" -> {
                image_type.load("", R.mipmap.icon_complain_8)
                text_type.text = getString(R.string.complain_complain_type_8)
            }
            "9" -> {
                image_type.load("", R.mipmap.icon_complain_9)
                text_type.text = getString(R.string.complain_complain_type_9)
            }
        }

        context?.let {
            adapter = ComplainChatListAdapter(it)
            adapter.setRecyclerView(chat_list)

            imageListAdapter = ImageListAdapter(it)
            imageListAdapter.setRecyclerView(complain_image_list)
        }

        try {
            mapview.onCreate(savedInstanceStates)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapview.getMapAsync(this)

        checkPermission()

        val texttype = getString(R.string.complain_complain_type_text) + " : " + text_type.text.toString()
        text_complain_type.text = texttype

        val timeMill = AppUtils.dateStringToMillis(listData.create_datetime.toString(), arrayOf(AppUtils.formateDate0))
        text_complain_date.text = AppUtils.getDateString(AppUtils.formateDate5, timeMill)

        val textdetail = getString(R.string.coomon_text_details) + " : " + when {
            listData.complain_detail.isNullOrEmpty() -> "-"
            listData.complain_detail == "null" -> "-"
            else -> listData.complain_detail.toString()
        }
        text_complain_detail.text = textdetail

        val textlocation = getString(R.string.complain_complain_location_text) + " : " + when {
            listData.remark.isNullOrEmpty() -> "-"
            listData.remark == "null" -> "-"
            else -> listData.remark.toString()
        }
        text_complain_location.text = textlocation

        val textstatus = getString(R.string.coomon_status_details) + " : " + when {
            listData.complain_status_name.isNullOrEmpty() -> "-"
            listData.complain_status_name == "null" -> "-"
            else -> listData.complain_status_name.toString()
        }
        text_complain_status.text = textstatus

        presenter = ComplainDetailFragmentPresenter(this)

        presenter.getDetailByID(listData.complain_id ?: "0")

        submit.setOnClickListener {
            if (input_message.text.toString().isNotEmpty()) {
                presenter.createDialog(listData.complain_id ?: "0", input_message.text.toString())
                input_message.setText("")
            }
            hideSoftKeyboard()
        }

        show_chat.setOnClickListener {
            incident_sliding_layout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            clearChat()
            updateNotification()
        }

        val newParams = SlidingUpPanelLayout.LayoutParams(SlidingUpPanelLayout.LayoutParams.MATCH_PARENT, CoreApplication.getScreenHeight() / 2)

        dragView_chat.layoutParams = newParams

        if (open_chat) {
            open_chat = false
            show_chat.performClick()
        }

        main_layout.setOnClickListener {
            incident_sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }
    }

    private fun checkPermission() {
        activity?.let {
            if (!isGrantedPermission()) {
                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            }
        }
    }

    internal inner class MyInfoWindowAdapter : MapboxMap.InfoWindowAdapter {

        private val myContentsView: View = layoutInflater.inflate(R.layout.custom_infowindow, null)

        override fun getInfoWindow(marker: Marker): View? {
//            val tvTitle = myContentsView.findViewById(R.id.name) as TextView
//            tvTitle.text = marker.title
//            return myContentsView
            return null
        }

    }

    private fun getLocation(): LatLng {
        val location = getCurrentLocation()
        return if (location != null) {
            LatLng(location.latitude, location.longitude)
        } else {
            LatLng(16.054158, 103.652355)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(): Location? {
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        var gps_enabled = false

        try {
            gps_enabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        } catch (ex: Exception) {
        }

        if (!gps_enabled) {
            Toast.makeText(context, "please enable gps", Toast.LENGTH_LONG).show()
        }

        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        locationManager?.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)

        return locationManager?.getLastKnownLocation(locationManager?.getBestProvider(criteria, false))
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location?) {
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        }

        override fun onProviderEnabled(p0: String?) {
        }

        override fun onProviderDisabled(p0: String?) {
        }

    }

    private fun setMarker(lngYou: LatLng, title: String) {
        val marker = MarkerOptions()
                .title(title)
                .position(LatLng(lngYou.latitude, lngYou.longitude))
                .icon(bitmapDescriptorFromVector(activity, R.mipmap.icon_pin_101))
        mapboxMap?.let {
            it.addMarker(marker)
            it.selectMarker(marker.marker)
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

    override fun onStart() {
        super.onStart()
        mapview?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapview?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapview?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapview?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapview?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapview?.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapview?.onDestroy()
        locationManager?.removeUpdates(locationListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview?.onDestroy()
        locationManager?.removeUpdates(locationListener)
    }

    fun onBackPressed() {
        if (incident_sliding_layout.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            incident_sliding_layout.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        } else {
            activity?.apply {
                finish()
            }
        }
    }

    fun updateNotification() {
        if (incident_sliding_layout?.panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
            clearChat()
        }

        val complain_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.COMPLAIN_CHAT_DB)
                ?: arrayListOf()
        var count = 0

        for (data in complain_db) {
            if (listData.complain_id == data["complain_id"]) {
                count += (data["count"] ?: "0").toInt()
            }
        }

        text_message_size?.text = count.toString()

        if (count > 0) {
            text_message_size?.visibility = View.VISIBLE
        } else {
            text_message_size?.visibility = View.GONE
        }

        presenter.getDetailByID(listData.complain_id ?: "0")
    }

    private fun clearChat() {
        val complain_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.COMPLAIN_CHAT_DB)
                ?: arrayListOf()
        val h = hashMapOf<String, String>()
        h["complain_id"] = listData.complain_id ?: "0"
        h["count"] = "0"
        for (data in complain_db) {
            if (listData.complain_id == data["complain_id"]) {
                complain_db.remove(data)
                break
            }
        }
        complain_db.add(h)
        Hawk.delete(Const.COMPLAIN_CHAT_DB)
        Hawk.put(Const.COMPLAIN_CHAT_DB, complain_db)

        val dialog_id = arrayListOf<String>()
        for (i in adapter.list) {
            dialog_id.add(i.complain_dialog_id ?: "")
        }

        presenter.updateComplainDialog(listData.complain_id ?: "", dialog_id)
    }
}
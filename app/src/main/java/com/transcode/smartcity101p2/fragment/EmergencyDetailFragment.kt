package com.transcode.smartcity101p2.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_emergency_detail.*
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
import com.transcode.smartcity101p2.adapter.EmergencyChatListAdapter
import com.transcode.smartcity101p2.adapter.ImageListAdapter
import com.transcode.smartcity101p2.contract.EmergencyDetailFragmentContract
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.emergency.EmergencyByIDResponse
import com.transcode.smartcity101p2.model.emergency.EmergencyListResponse
import com.transcode.smartcity101p2.presenter.EmergencyDetailFragmentPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import kotlin.math.roundToInt

class EmergencyDetailFragment : CoreFragment(), EmergencyDetailFragmentContract.View, OnMapReadyCallback {
    override fun createDialogSuccess() {
        presenter.getDetailByID(listData.emer_id ?: "0")
    }

    override fun updateView(data: EmergencyByIDResponse.EmergencyByIDResponseData) {
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
            val textcode = getString(R.string.common_text_emer_code) + " : " + when {
                data.emer_code.isNullOrEmpty() -> "-"
                data.emer_code == "null" -> "-"
                else -> data.emer_code.toString()
            }
            text_emegency_code.text = textcode
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

        val newlatlng = LatLng(listData.emer_lat?.toDouble() ?: 16.054158, listData.emer_lng?.toDouble()
                ?: 103.6501564)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(newlatlng, 12.0)
        map?.moveCamera(cameraUpdate)
        map?.infoWindowAdapter = MyInfoWindowAdapter()

        setMarker(newlatlng, getString(R.string.emergency_emergency_event_location))
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
        fun newInstance(data: EmergencyListResponse.EmergencyListResponseData, open_chat: Boolean): EmergencyDetailFragment {
            return EmergencyDetailFragment().apply {
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
    lateinit var listData: EmergencyListResponse.EmergencyListResponseData
    lateinit var presenter: EmergencyDetailFragmentPresenter
    lateinit var adapter: EmergencyChatListAdapter
    lateinit var imageListAdapter: ImageListAdapter
    var open_chat = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.savedInstanceStates = savedInstanceState
        return inflater.inflate(R.layout.fragment_emergency_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val bundle = savedInstanceState ?: arguments
        bundle?.let {
            if (it.containsKey("data")) {
                val gson = Gson()
                listData = gson.fromJson(it.getString("data"), EmergencyListResponse.EmergencyListResponseData::class.java)
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

        when (listData.emer_type_id) {
            "1" -> {
                image_type.load("", R.mipmap.icona_077)
                text_type.text = getString(R.string.emergency_emergency_type_1)
            }
            "2" -> {
                image_type.load("", R.mipmap.icona_088)
                text_type.text = getString(R.string.emergency_emergency_type_2)
            }
            "3" -> {
                image_type.load("", R.mipmap.icona_099)
                text_type.text = getString(R.string.emergency_emergency_type_3)
            }
            "4" -> {
                image_type.load("", R.mipmap.icona_1010)
                text_type.text = getString(R.string.emergency_emergency_type_4)
            }
            "5" -> {
                image_type.load("", R.mipmap.icona_1111)
                text_type.text = getString(R.string.emergency_emergency_type_5)
            }
            "6" -> {
                image_type.load("", R.mipmap.icona_1212)
                text_type.text = getString(R.string.emergency_emergency_type_6)
            }
        }

        context?.let {
            adapter = EmergencyChatListAdapter(it)
            adapter.setRecyclerView(chat_list)

            imageListAdapter = ImageListAdapter(it)
            imageListAdapter.setRecyclerView(emer_image_list)
        }

        try {
            mapview.onCreate(savedInstanceStates)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapview.getMapAsync(this)

        checkPermission()

        val texttype = getString(R.string.emergency_emergency_type_text) + " : " + when (listData.emer_type_id) {
            "1" -> getString(R.string.emergency_emergency_type_1)
            "2" -> getString(R.string.emergency_emergency_type_2)
            "3" -> getString(R.string.emergency_emergency_type_3)
            "4" -> getString(R.string.emergency_emergency_type_4)
            "5" -> getString(R.string.emergency_emergency_type_5)
            "6" -> getString(R.string.emergency_emergency_type_6)
            else -> ""

        }
        text_emegency_type.text = texttype

        val timeMill = AppUtils.dateStringToMillis(listData.create_datetime.toString(), arrayOf(AppUtils.formateDate0))
        text_emegency_date.text = AppUtils.getDateString(AppUtils.formateDate5, timeMill)

        val textdetail = getString(R.string.coomon_text_details) + " : " + when {
            listData.emer_detail.isNullOrEmpty() -> "-"
            listData.emer_detail == "null" -> "-"
            else -> listData.emer_detail.toString()
        }
        text_emegency_detail.text = textdetail

        val textlocation = getString(R.string.emergency_emergency_location_text) + " : " + when {
            listData.remark.isNullOrEmpty() -> "-"
            listData.remark == "null" -> "-"
            else -> listData.remark.toString()
        }
        text_emergency_location.text = textlocation

        val textstatus = getString(R.string.coomon_status_details) + " : " + when {
            listData.emer_status_name.isNullOrEmpty() -> "-"
            listData.emer_status_name == "null" -> "-"
            else -> listData.emer_status_name.toString()
        }
        text_emegency_status.text = textstatus

        presenter = EmergencyDetailFragmentPresenter(this)

        presenter.getDetailByID(listData.emer_id ?: "0")

        submit.setOnClickListener {
            if (input_message.text.toString().isNotEmpty()) {
                presenter.createDialog(listData.emer_id ?: "0", input_message.text.toString())
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
            for (m in it.markers) {
                if (m.title == marker.title) {
                    it.selectMarker(m)
                }
            }
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

        val emer_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.EMER_CHAT_DB)
                ?: arrayListOf()
        var count = 0

        for (data in emer_db) {
            if (listData.emer_id == data["emer_id"]) {
                count += (data["count"] ?: "0").toInt()
            }
        }

        text_message_size?.text = count.toString()

        if (count > 0) {
            text_message_size?.visibility = View.VISIBLE
        } else {
            text_message_size?.visibility = View.GONE
        }

        presenter.getDetailByID(listData.emer_id ?: "0")
    }

    private fun clearChat() {
        val emer_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.EMER_CHAT_DB)
                ?: arrayListOf()
        val h = hashMapOf<String, String>()
        h["emer_id"] = listData.emer_id ?: "0"
        h["count"] = "0"
        for (data in emer_db) {
            if (listData.emer_id == data["emer_id"]) {
                emer_db.remove(data)
                break
            }
        }
        emer_db.add(h)
        Hawk.delete(Const.EMER_CHAT_DB)
        Hawk.put(Const.EMER_CHAT_DB, emer_db)

        val dialog_id = arrayListOf<String>()
        for (i in adapter.list) {
            dialog_id.add(i.emer_dialog_id ?: "")
        }

        presenter.updateEmerDialog(listData.emer_id ?: "", dialog_id)
    }
}
package com.transcode.smartcity101p2.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.transcode.smartcity101p2.BookQueueActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.contract.LocationFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.presenter.LocationFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_select_location.*
import kotlin.math.roundToInt

class SelectLocationFragment : CoreFragment(), LocationFragmentContract.View, OnMapReadyCallback {
    override fun onMapReady(map: MapboxMap) {
        mapboxMap = map
        context?.let {
            setUpMap(it)
        }
        mapboxMap?.isAllowConcurrentMultipleOpenInfoWindows = true

        val latLng = if (isGrantedPermission()) {
            getLocation()
        } else {
            LatLng(16.054158, 103.652355)
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
        map?.moveCamera(cameraUpdate)
        map?.infoWindowAdapter = MyInfoWindowAdapter()

        map?.addOnMapClickListener {
            click_latlng = it
            for (mark in map.markers) {
                if (mark.title == "สถานที่รับบริการ") map.removeMarker(mark)
            }
            setMarker(it, "สถานที่รับบริการ")

            for (mark in map.markers) {
                mapboxMap?.let {
                    it.selectMarker(mark)
                }
            }
            false
        }
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
        fun newInstance(queue_id: String, choose_datatime: String, queue_slot_id: String, queue_title: String, endtime: String, form_id: String): SelectLocationFragment {
            return SelectLocationFragment().apply {
                val bundle = Bundle()
                bundle.putString("queue_id", queue_id)
                bundle.putString("queue_slot_id", queue_slot_id)
                bundle.putString("choose_datatime", choose_datatime)
                bundle.putString("queue_title", queue_title)
                bundle.putString("endtime", endtime)
                bundle.putString("form_id", form_id)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: LocationFragmentPresenter
    private var savedInstanceStates: Bundle? = null
    private var mapboxMap: MapboxMap? = null
    private var first = true
    private var locationManager: LocationManager? = null
    private var click_latlng: LatLng? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.savedInstanceStates = savedInstanceStates
        return inflater.inflate(R.layout.fragment_select_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        click_latlng = null
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle("กำหนดสถานที่รับบริการ")
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }

        try {
            mapview.onCreate(savedInstanceStates)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapview.getMapAsync(this)

        presenter = LocationFragmentPresenter(this)

        submit.setOnClickListener {

            click_latlng?.let {
                val lat = it.latitude.toString()
                val lng = it.longitude.toString()
                val queue_id = arguments?.getString("queue_id") ?: "1"
                val queue_slot_id = arguments?.getString("queue_slot_id") ?: "1"
                val choose_datatime = arguments?.getString("choose_datatime") ?: "1"
                val queue_title = arguments?.getString("queue_title") ?: ""
                val endtime = arguments?.getString("endtime") ?: ""
                val form_id = arguments?.getString("form_id") ?: ""

                activity?.apply {
                    val intent = Intent(this, BookQueueActivity::class.java)
                    intent.putExtra("queue_type", "3")
                    intent.putExtra("queue_id", queue_id)
                    intent.putExtra("choose_datatime", choose_datatime)
                    intent.putExtra("serve_lat", lat)
                    intent.putExtra("serve_lng", lng)
                    intent.putExtra("queue_slot_id", queue_slot_id)
                    intent.putExtra("queue_title", queue_title)
                    intent.putExtra("endtime", endtime)
                    intent.putExtra("form_id", form_id)
                    startActivityForResult(intent, Const.REQUEST_CODE_BOOK_QUEUE)
                }
            }
        }
    }

    private fun setMarker(lngYou: LatLng, title: String) {
        val pin = if (title == "คุณอยู่ที่นี่") {
            R.mipmap.youhere
        } else {
            R.mipmap.pin2
        }
        val marker = MarkerOptions()
                .title(title)
                .position(LatLng(lngYou.latitude, lngYou.longitude))
                .icon(bitmapDescriptorFromVector(activity, pin))
        mapboxMap?.let {
            it.addMarker(marker)
//            it.selectMarker(marker.marker)
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

    private fun bitmapDescriptorFromVector(context: Context?, vectorResId: Int): Icon {
        val w = (100 * 0.8).roundToInt()
        val h = (120 * 0.8).roundToInt()

        val vectorDrawable = ContextCompat.getDrawable(context!!, vectorResId)
        vectorDrawable!!.setBounds(0, 0, w, w)
        val bitmap = Bitmap.createBitmap(w, w, Bitmap.Config.ARGB_8888)
        val resizeBitmap = Bitmap.createScaledBitmap(bitmap, w, w, false)
        val canvas = Canvas(resizeBitmap)
        vectorDrawable.draw(canvas)
        return IconFactory.getInstance(context).fromBitmap(resizeBitmap)
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

//        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10.0f, locationListener)
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        locationManager?.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)

        return locationManager?.getLastKnownLocation(locationManager?.getBestProvider(criteria, false))
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location?) {
            val latLng = LatLng(p0?.latitude ?: 16.054158, p0?.longitude ?: 103.6501564)
            mapboxMap?.let {

                for (mark in it.markers) {
                    if (mark.title == "คุณอยู่ที่นี่")
                        it.removeMarker(mark)
                }
                setMarker(latLng, "คุณอยู่ที่นี่")
                mapboxMap?.let {
                    for (mark in it.markers) {
                        if (mark.title == "คุณอยู่ที่นี่") {
                            it.selectMarker(mark)
                        }
                    }
                }
                if (first) {
                    first = false
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
                    mapboxMap?.moveCamera(cameraUpdate)
                }
            }
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }

    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapview.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapview.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapview.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapview.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapview.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapview.onDestroy()
        locationManager?.removeUpdates(locationListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview?.onDestroy()
        locationManager?.removeUpdates(locationListener)
    }
}
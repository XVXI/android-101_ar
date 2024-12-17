package com.transcode.smartcity101p2.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.contract.LocationFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.presenter.LocationFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_location.*
import kotlin.math.roundToInt

class LocationFragment : CoreFragment(), LocationFragmentContract.View, OnMapReadyCallback {
    override fun onMapReady(map: MapboxMap) {
        mapboxMap = map
        context?.let {
            setUpMap(it)
        }
        mapboxMap?.isAllowConcurrentMultipleOpenInfoWindows = true

        val destLatLan = LatLng(arguments?.getDouble("destLat")
                ?: 16.054158, arguments?.getDouble("destLan") ?: 103.6501564)

        val latLng = if (isGrantedPermission()) {
            getLocation()
        } else {
            LatLng(16.054158, 103.652355)
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(destLatLan, 12.0)
        map?.moveCamera(cameraUpdate)
        map?.infoWindowAdapter = MyInfoWindowAdapter()

        setMarker(destLatLan, "จุดรับบริการ")

        mapboxMap?.let {
            for (mark in it.markers) {
                it.selectMarker(mark)
            }
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
        fun newInstance(destLat: Double, destLan: Double): LocationFragment {
            return LocationFragment().apply {
                val bundle = Bundle()
                bundle.putDouble("destLat", destLat)
                bundle.putDouble("destLan", destLan)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: LocationFragmentPresenter
    private var savedInstanceStates: Bundle? = null
    private var mapboxMap: MapboxMap? = null
    private lateinit var locationManager: LocationManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.savedInstanceStates = savedInstanceState
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle("สถานที่รับบริการ")
        appBar.leftBt.setOnClickListener {
            //            fragmentManager?.popBackStack()
            FragmentHelper.remove(fragmentManager, this)
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
    }

    private fun setMarker(lngYou: LatLng, lngPos: LatLng) {
        mapboxMap?.addMarker(MarkerOptions()
                .title("คุณอยู่ที่นี่")
                .position(LatLng(lngYou.latitude, lngYou.longitude))
                .icon(bitmapDescriptorFromVector(activity, R.mipmap.pin1)))


        val marker = MarkerOptions()
                .title("สถานที่รับบริการ")
                .position(LatLng(lngPos.latitude, lngPos.longitude))
                .icon(bitmapDescriptorFromVector(activity, R.mipmap.pin2))
        mapboxMap?.let {
            it.addMarker(marker).showInfoWindow(it, mapview)
        }
    }

    private fun setMarker(lngYou: LatLng, title: String) {
        val marker = MarkerOptions()
                .title(title)
                .position(LatLng(lngYou.latitude, lngYou.longitude))
                .icon(bitmapDescriptorFromVector(activity, R.mipmap.icon_pin_101))
        mapboxMap?.let {
            it.addMarker(marker)
//            if (title == "จุดรับบริการ") {
//                it.selectMarker(marker.marker)
//            }
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
        vectorDrawable!!.setBounds(0, 0, w, h)
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val resizeBitmap = Bitmap.createScaledBitmap(bitmap, w, h, false)
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
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        if (!gps_enabled) {
            Toast.makeText(context, "please enable gps", Toast.LENGTH_LONG).show()
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10.0f, locationListener)
        val criteria = Criteria()
        return locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false))
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
        locationManager.removeUpdates(locationListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview?.onDestroy()
        locationManager.removeUpdates(locationListener)
    }

    fun backPress() {
        val appBar = appbar as CustomAppBarLayout
        appBar.leftBt.performClick()
    }
}
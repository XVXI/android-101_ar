package com.transcode.smartcity101p2.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.transcode.smartcity101p2.CCtvPlayerActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.contract.LocationFragmentContract
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.presenter.LocationFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_location.*
import kotlin.math.roundToInt

class CctvLocationFragment : CoreFragment(), LocationFragmentContract.View, OnMapReadyCallback {
    override fun onMapReady(map: MapboxMap) {
        mapboxMap = map
        context?.let {
            setUpMap(it)
        }
        mapboxMap?.isAllowConcurrentMultipleOpenInfoWindows = true

        val destLatLan = LatLng(arguments?.getDouble("destLat")
                ?: 16.054158, arguments?.getDouble("destLan") ?: 103.6501564)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(destLatLan, 12.0)
        map?.moveCamera(cameraUpdate)
        map?.infoWindowAdapter = MyInfoWindowAdapter()

        setMarker(destLatLan, place_title)

        mapboxMap?.let {
            for (mark in it.markers) {
                if (mark.title == place_title) {
                    it.selectMarker(mark)
                }
            }
        }

        mapboxMap?.setOnInfoWindowClickListener {
            activity?.apply {
                val stream_url = arguments?.getString("stream_url") ?: ""
                val intent = Intent(this, CCtvPlayerActivity::class.java)
                intent.putExtra("stream_url", stream_url)
                startActivity(intent)
            }
            true
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
        fun newInstance(destLat: Double, destLan: Double, header_title: String, place_title: String, stream_url: String): CctvLocationFragment {
            return CctvLocationFragment().apply {
                val bundle = Bundle()
                bundle.putDouble("destLat", destLat)
                bundle.putDouble("destLan", destLan)
                bundle.putString("header_title", header_title)
                bundle.putString("place_title", place_title)
                bundle.putString("stream_url", stream_url)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: LocationFragmentPresenter
    private var savedInstanceStates: Bundle? = null
    private var mapboxMap: MapboxMap? = null
    private var place_title = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.savedInstanceStates = savedInstanceState
        return inflater.inflate(R.layout.fragment_location, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        place_title = arguments?.getString("place_title") ?: ""
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(arguments?.getString("header_title") ?: "CCTV")
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
    }

    private fun setMarker(lngYou: LatLng, title: String) {
        val marker = MarkerOptions()
                .title(title)
                .position(LatLng(lngYou.latitude, lngYou.longitude))
                .icon(bitmapDescriptorFromVector(activity, R.mipmap.icon_pin_101))
        mapboxMap?.let {
            it.addMarker(marker)
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
    }

    override fun onDestroy() {
        super.onDestroy()
        mapview?.onDestroy()
    }
}
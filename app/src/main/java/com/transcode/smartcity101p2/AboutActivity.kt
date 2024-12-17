package com.transcode.smartcity101p2

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.Marker
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import com.mapbox.mapboxsdk.maps.Style
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.appbar_main.appbar
import kotlinx.android.synthetic.main.appbar_main.view.*
import android.R.attr.versionName
import com.transcode.smartcity101p2.model.Const
import kotlin.math.roundToInt


class AboutActivity : CoreActivity(), OnMapReadyCallback {

    override fun onMapReady(map: MapboxMap) {
        mapboxMap = map
        setUpMap(this)
        val latLng = LatLng(16.0531951, 103.6501564)
        setMarker(latLng, "สถานที่")
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
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

    private var mapboxMap: MapboxMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle("เกี่ยวกับเรา")
        appBar.leftBt.setOnClickListener {
            finish()
        }

        try {
            mapview.onCreate(savedInstanceState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapview.getMapAsync(this)

        val version = try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            "0"
        }

        version_name.text = "Version. " + version + " " + BuildConfig.ENV
    }

    private fun setMarker(lngYou: LatLng, title: String) {
        val marker = MarkerOptions()
                .title(title)
                .position(LatLng(lngYou.latitude, lngYou.longitude))
                .icon(bitmapDescriptorFromVector(this, R.mipmap.icon_pin_101))
        mapboxMap?.let {
            it.addMarker(marker)
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

    override fun onDestroy() {
        super.onDestroy()
        mapview?.onDestroy()
    }

    internal inner class MyInfoWindowAdapter : MapboxMap.InfoWindowAdapter {

        private val myContentsView: View = layoutInflater.inflate(R.layout.custom_infowindow, null)

        override fun getInfoWindow(marker: Marker): View? {
            return null
        }

    }
}
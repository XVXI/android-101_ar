package com.transcode.smartcity101p2.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
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
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.extension.concurrencyFormat
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.dialog_market_confirm_cart.view.*
import kotlinx.android.synthetic.main.dialog_market_confirm_cart.view.appbar
import kotlin.math.roundToInt

class MarketConfirmBuyDialog(context: Context) : CoreDialog(context), OnMapReadyCallback {

    var b: View? = null
    var first = true

    private var savedInstanceState: Bundle? = null
    private var mapboxMap: MapboxMap? = null
    private var locationManager: LocationManager? = null
    private var click_latlng: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        initLayout()
    }

    private fun initLayout() {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.dialog_market_confirm_cart, null, false)
        val lp = FrameLayout.LayoutParams(CoreApplication.getScreenWidth(), CoreApplication.getScreenHeight() - getStatusBarHeight())
        b?.layouts?.layoutParams = lp
        b?.let {
            setContentView(it)
        }

        b?.dl_checked?.setOnClickListener {
            b?.dl_checked?.isChecked = true
        }

        b?.dl_close?.setOnClickListener {
            dismiss()
        }

        try {
            b?.mapView?.onCreate(savedInstanceState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        b?.mapView?.getMapAsync(this)

        b?.b_bar?.visibility = View.GONE

        if (hasNavBar()) {
            val lp2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getNavigationH())
            b?.b_bar?.visibility = View.VISIBLE
            b?.b_bar?.layoutParams = lp2
        }

        b?.let {
            val appBar = it.appbar as CustomAppBarLayout
            appBar.setTitle("ยืนยันคำสั่งซื้อ")
            appBar.leftBt.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = context.resources.getDimensionPixelSize(resourceId)
        }
        return result;
    }

    private fun hasNavBar(): Boolean {
        val resources = context.resources
        val id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    private fun getNavigationH(): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId)
        }
        return 0
    }

    override fun onMapReady(map: MapboxMap) {
        map.setStyle(Style.Builder().fromUri(Const.MAP_STYLE_URI), Style.OnStyleLoaded {

        })
        mapboxMap = map

        val latLng = if (isGrantedPermission()) {
            getLocation()
        } else {
            LatLng(16.054158, 103.652355)
        }

        click_latlng = latLng

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
        mapboxMap?.moveCamera(cameraUpdate)
        setMarker(latLng, "สถานที่")

        map?.infoWindowAdapter = MyInfoWindowAdapter()
        map?.addOnMapClickListener {
            click_latlng = it
            for (mark in map?.markers) {
                if (mark.title == "สถานที่") map?.removeMarker(mark)
            }
            setMarker(it, "สถานที่")
            false
        }
    }

    fun getLatLng(): LatLng? = click_latlng

    internal inner class MyInfoWindowAdapter : MapboxMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {
            return null
        }
    }

    fun isGrantedPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)

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
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
                    if (mark.title == "สถานที่") it.removeMarker(mark)
                }
                setMarker(latLng, "สถานที่")
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

    private fun setMarker(lngYou: LatLng, title: String) {
        val marker = MarkerOptions()
                .title(title)
                .position(LatLng(lngYou.latitude, lngYou.longitude))
                .icon(bitmapDescriptorFromVector(context, R.mipmap.icon_pin_101))
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

    fun setPrice(price: String) {
        val p = price.concurrencyFormat()
        b?.dl_text_all_price?.text = "$p บาท"
    }
}
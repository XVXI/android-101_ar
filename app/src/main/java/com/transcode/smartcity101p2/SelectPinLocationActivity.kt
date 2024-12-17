package com.transcode.smartcity101p2

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
import android.view.View
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
import com.transcode.smartcity101p2.model.Const
import kotlinx.android.synthetic.main.activity_select_pin_location.*
import kotlin.math.roundToInt

class SelectPinLocationActivity : CoreActivity(), OnMapReadyCallback {
    override fun onMapReady(map: MapboxMap) {
        mapboxMap = map
        setUpMap(this)

        val latLng = if (isGrantedPermission()) {
            getLocation()
        } else {
            LatLng(16.054158, 103.652355)
        }
        click_latlng = latLng
        setMarker(latLng, getString(R.string.emergency_emergency_event_location))
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
        map?.moveCamera(cameraUpdate)
        map?.infoWindowAdapter = MyInfoWindowAdapter()

        map?.addOnMapClickListener {
            click_latlng = it
            for (mark in map?.markers) {
                if (mark.title == getString(R.string.emergency_emergency_event_location)) map?.removeMarker(mark)
            }
            setMarker(it, getString(R.string.emergency_emergency_event_location))
            false
        }
    }

    private var mapboxMap: MapboxMap? = null
    private var locationManager: LocationManager? = null
    private var click_latlng: LatLng? = null
    private var first = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_pin_location)

        try {
            mapview.onCreate(savedInstanceState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapview.getMapAsync(this)

        root.setOnClickListener {
            finish()
        }

        text_message.text = intent?.extras?.getString("title") ?: ""

        card.setOnClickListener {}

        pined.setOnClickListener {
            val intent = Intent()
            intent.putExtra("lat", click_latlng?.latitude ?: 16.054158)
            intent.putExtra("lng", click_latlng?.longitude ?: 103.652355)
            setResult(Activity.RESULT_OK, intent)
            finish()
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

    fun isGrantedPermission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)

    }

    private fun getLocation(): LatLng {
        val location = if (isGrantedPermission()) {
            getLocation()
        } else {
            LatLng(16.054158, 103.652355)
        }
        return LatLng(location.latitude, location.longitude)
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
        locationManager?.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)

        return locationManager?.getLastKnownLocation(locationManager?.getBestProvider(criteria, false))
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location?) {
            val latLng = LatLng(p0?.latitude ?: 16.054158, p0?.longitude ?: 103.652355)
            click_latlng = latLng
            mapboxMap?.let {
                for (mark in it.markers) {
                    if (mark.title == getString(R.string.emergency_emergency_event_location)) it.removeMarker(mark)
                }
                setMarker(latLng, getString(R.string.emergency_emergency_event_location))
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
                .icon(bitmapDescriptorFromVector(this, R.mipmap.icon_pin_101))
        mapboxMap?.let {
            it.addMarker(marker)
//            it.selectMarker(marker.marker)
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
        locationManager?.removeUpdates(locationListener)
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
}
package com.transcode.smartcity101p2.dialog

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
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
import com.transcode.smartcity101p2.model.Const
import kotlinx.android.synthetic.main.dialog_market_shop_location.view.*
import kotlin.math.roundToInt

class MarketShopLocationDialog(context: Context, val latLng: LatLng, val shopName: String) : CoreDialog(context), OnMapReadyCallback {

    var b: View? = null

    private var savedInstanceState: Bundle? = null
    private var mapboxMap: MapboxMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        initLayout()
    }

    private fun initLayout() {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        b = inflater.inflate(R.layout.dialog_market_shop_location, null, false)
        setContentView(b)

        b?.dl_close?.setOnClickListener {
            dismiss()
        }

        try {
            b?.mapView?.onCreate(savedInstanceState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        b?.mapView?.getMapAsync(this)
    }

    override fun onMapReady(map: MapboxMap) {
        map.setStyle(Style.Builder().fromUri(Const.MAP_STYLE_URI), Style.OnStyleLoaded {

        })
        mapboxMap = map

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
        mapboxMap?.moveCamera(cameraUpdate)
        setMarker(latLng, shopName)

        map?.infoWindowAdapter = MyInfoWindowAdapter()
    }

    internal inner class MyInfoWindowAdapter : MapboxMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {
            return null
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
}
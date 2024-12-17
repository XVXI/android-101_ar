package com.transcode.smartcity101p2.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
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
import com.transcode.smartcity101p2.CCtvLocationActivity
import com.transcode.smartcity101p2.CCtvPlayerActivity
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.adapter.CctvListAdapter
import com.transcode.smartcity101p2.contract.CctvListFragmentContract
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.model.CctvResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.presenter.CctvListFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_cctvlist.*
import kotlin.math.roundToInt

class CctvListFragment : CoreFragment(), CctvListFragmentContract.View, CctvListAdapter.ClickItem, OnMapReadyCallback {

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

        bt_list.setOnClickListener {
            MODE = MODE_LIST
            bt_list.isEnabled = false
            bt_map.isEnabled = true

            getList()
        }

        bt_map.setOnClickListener {
            MODE = MODE_MAP
            bt_list.isEnabled = true
            bt_map.isEnabled = false

            getList()
        }

        bt_list.performClick()
    }

    private fun getList() {
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        val city_id = login.authority_info?.CityId ?: ""
        val token = login.authority_info?.getAllToken() ?: ""

        loadingDialog?.show()
        presenter.getCctvList(city_id, token)
    }

    private fun setUpMap(context: Context) {
        mapboxMap?.setStyle(Style.Builder().fromUri(Const.MAP_STYLE_URI), Style.OnStyleLoaded {

        })

//        if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
//                shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
//            showLocationPermissionReasonable()
//        } else {
        ActivityCompat.requestPermissions(context as Activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
//        }
    }

    private fun showLocationPermissionReasonable() {
        val alertBuilder = AlertDialog.Builder(context, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle("Use your location")
        alertBuilder.setMessage("This app collects location data to see your current location in map [" + getString(R.string.home_emergency) + "] , " +
                "[" + getString(R.string.home_complain) + "] , " +
                "[" + getString(R.string.home_cctv) + "] & " +
                "[" + getString(R.string.home_travel) + "] even when the app is closed or not in use.")
        alertBuilder.setPositiveButton("Turn on", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                ActivityCompat.requestPermissions(context as Activity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
            }
        })

        alertBuilder.setNegativeButton("No thanks", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

            }
        })

        val alert = alertBuilder.create()
        alert.show()
    }

    override fun onclickItem(res: CctvResponse.CctvData) {
        activity?.apply {
            val stream_url = res.Url ?: ""
            val intent = Intent(this, CCtvPlayerActivity::class.java)
            intent.putExtra("stream_url", stream_url)
            startActivity(intent)
        }
    }

    override fun onclickSubItem(res: CctvResponse.CctvData) {
        activity?.apply {
            val lat = res.Lat ?: "0"
            val lng = res.Lng ?: "0"
            val header_title = arguments?.getString("header_title") ?: "CCTV"
            val place_title = res.Cctvname ?: "cctvname"
            val stream_url = res.Url ?: ""
            val intent = Intent(this, CCtvLocationActivity::class.java)
            intent.putExtra("lat", lat.toDouble())
            intent.putExtra("lng", lng.toDouble())
            intent.putExtra("header_title", header_title)
            intent.putExtra("place_title", place_title)
            intent.putExtra("stream_url", stream_url)
            startActivity(intent)
        }
    }

    override fun updateList(list: ArrayList<CctvResponse.CctvData>) {
        loadingDialog?.dismiss()

        mapview.visibility = View.INVISIBLE
        recyclerview.visibility = View.GONE
        text_empty.visibility = View.GONE

        if (list.size > 0) {
            if (MODE == MODE_MAP) {
                mapview.visibility = View.VISIBLE
                for (data in list) {
                    setMarker(data)
                }
                mapboxMap?.let {
                    for (mark in it.markers) {
                        it.selectMarker(mark)
                    }
                }

                val lat = list[(list.size - 1) / 2].Lat ?: "0"
                val lng = list[(list.size - 1) / 2].Lng ?: "0"
                val destLatLan = LatLng(lat.toDouble(), lng.toDouble())

                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(destLatLan, 12.0)
                mapboxMap?.moveCamera(cameraUpdate)

                mapboxMap?.setOnInfoWindowClickListener {
                    val maker = it

                    activity?.apply {
                        val m_lat = maker.position.latitude
                        val m_lng = maker.position.longitude
                        for (data in list) {
                            val d_lat = data.Lat ?: "0"
                            val d_lng = data.Lng ?: "0"
                            if (d_lat.toDouble() == m_lat && d_lng.toDouble() == m_lng) {
                                var stream_url = data.Url ?: ""
                                val intent = Intent(this, CCtvPlayerActivity::class.java)
                                intent.putExtra("stream_url", stream_url)
                                startActivity(intent)
                                break
                            }
                        }
                    }
                    true
                }
            } else {
                recyclerview.visibility = View.VISIBLE
                adapter.setData(list)
                adapter.notifyDataSetChanged()
            }
        } else {
            text_empty.visibility = View.VISIBLE
        }
    }

    private fun setMarker(data: CctvResponse.CctvData) {
        val lat = data.Lat ?: "0"
        val lng = data.Lng ?: "0"
        val marker = MarkerOptions()
                .title(data.Cctvname.toString())
                .position(LatLng(lat.toDouble(), lng.toDouble()))
                .icon(bitmapDescriptorFromVector(activity, R.mipmap.icon_pin_101))
        mapboxMap?.let {
            it.addMarker(marker)
        }
    }

    companion object {
        fun newInstance(header_title: String): CctvListFragment {
            return CctvListFragment().apply {
                val bundle = Bundle()
                bundle.putString("header_title", header_title)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: CctvListFragmentPresenter
    lateinit var adapter: CctvListAdapter
    var loadingDialog: LoadingDialog? = null
    val MODE_LIST = 1
    val MODE_MAP = 2
    var MODE = MODE_LIST
    private var savedInstanceStates: Bundle? = null
    private var mapboxMap: MapboxMap? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.savedInstanceStates = savedInstanceState
        return inflater.inflate(R.layout.fragment_cctvlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(arguments?.getString("header_title") ?: "CCTV")
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }

        presenter = CctvListFragmentPresenter(this)

        context?.let {
            adapter = CctvListAdapter(it)
            adapter.setRecyclerView(recyclerview)
            adapter.setClickListener(this)

            loadingDialog = LoadingDialog(it)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog?.setCancelable(false)
        }

        try {
            mapview.onCreate(savedInstanceStates)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapview.getMapAsync(this)
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
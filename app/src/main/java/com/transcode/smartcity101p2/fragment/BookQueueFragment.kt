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
import android.view.Window
import android.widget.Toast
import com.google.gson.GsonBuilder
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.TicketDetailActivity
import com.transcode.smartcity101p2.adapter.AllQueueListAdapter
import com.transcode.smartcity101p2.contract.BookQueueFragmentContract
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.MyQueueResponse
import com.transcode.smartcity101p2.presenter.BookQueueFragmentPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_book_ticket_detail.*
import kotlin.math.roundToInt

class BookQueueFragment : CoreFragment(), BookQueueFragmentContract.View, OnMapReadyCallback {

    override fun onMapReady(map: MapboxMap) {
        mapboxMap = map
        context?.let {
            setUpMap(it)
        }

        val serve_lat = arguments?.getString("serve_lat") ?: "0"
        val serve_lng = arguments?.getString("serve_lng") ?: "0"

        val latLng = LatLng(serve_lat.toDouble(), serve_lng.toDouble())

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 15.0)
        map?.moveCamera(cameraUpdate)

        setMarker(latLng, "จุดรับบริการ")
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

        fun newInstance(queue_type: String, queue_id: String, choose_datatime: String, serve_lat: String, serve_lng: String, queue_slot_id: String, queue_title: String, endtime: String, form_id: String): BookQueueFragment {
            return BookQueueFragment().apply {
                val bundle = Bundle()
                bundle.putString("queue_type", queue_type)
                bundle.putString("queue_id", queue_id)
                bundle.putString("choose_datatime", choose_datatime)
                bundle.putString("serve_lat", serve_lat)
                bundle.putString("serve_lng", serve_lng)
                bundle.putString("queue_slot_id", queue_slot_id)
                bundle.putString("queue_title", queue_title)
                bundle.putString("endtime", endtime)
                bundle.putString("form_id", form_id)
                arguments = bundle
            }
        }
    }

    private var savedInstanceStates: Bundle? = null
    lateinit var presenter: BookQueueFragmentPresenter
    lateinit var adapter: AllQueueListAdapter
    private var mapboxMap: MapboxMap? = null
    private val space = "          "
    private val formatdate = "EEEE$space"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        savedInstanceStates = savedInstanceState
        return inflater.inflate(R.layout.fragment_book_ticket_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(arguments?.getString("queue_title") ?: "ยืนยันการจอง")
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }

        presenter = BookQueueFragmentPresenter(this)

        try {
            mapview.onCreate(savedInstanceStates)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapview.getMapAsync(this)

        val choose_datatime = arguments?.getString("choose_datatime") ?: "0"
        val dateTimeMill = AppUtils.dateStringToMillis(choose_datatime, arrayOf(AppUtils.formateDate5))
        var endtime = arguments?.getString("endtime") ?: "0"
        if (endtime.length >= 8) {
            endtime = endtime.substring(0, endtime.length - 3)
        }

        dateSelect.text = AppUtils.getDateString(formatdate + AppUtils.formateDate3, dateTimeMill)
        val text_time = AppUtils.getDateString(AppUtils.formateDate4, dateTimeMill) + " - " + endtime
        selecttime.text = text_time

        bookit.setOnClickListener {
            openBookPopup()
        }
    }

    private fun openBookPopup() {
        context?.let {
            val customDialog = CustomDialog(it)
            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            customDialog.show()
            customDialog.setTitle(getString(R.string.confirm_queue_title_text))
            customDialog.setMessage(getString(R.string.confirm_queue_message_text))
            customDialog.setOnClickOKListener(View.OnClickListener {
                customDialog.dismiss()
                val queue_type = arguments?.getString("queue_type") ?: ""
                val queue_id = arguments?.getString("queue_id") ?: ""
                val choose_datatime = arguments?.getString("choose_datatime") ?: ""
                val citizen_id = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA).authority_info?.CitizenId
                        ?: ""
                val serve_lat = arguments?.getString("serve_lat") ?: "0"
                val serve_lng = arguments?.getString("serve_lng") ?: "0"
                val queue_slot_id = arguments?.getString("queue_slot_id") ?: "0"
                val form_id = arguments?.getString("form_id") ?: ""
                presenter.bookQueue(queue_type, queue_id, choose_datatime, citizen_id, serve_lat, serve_lng, queue_slot_id, form_id)
            })
            customDialog.setOnClickCancelListener(View.OnClickListener { customDialog.dismiss() })
        }
    }

    override fun showError(message: String) {
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    private fun setMarker(lngYou: LatLng, title: String) {
        val marker = MarkerOptions()
                .title(title)
                .position(LatLng(lngYou.latitude, lngYou.longitude))
                .icon(bitmapDescriptorFromVector(activity, R.mipmap.pin2))
        mapboxMap?.addMarker(marker)
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

    override fun bookSuccess(myQueueResponse: MyQueueResponse) {
        activity?.apply {
            val intent = Intent(this, TicketDetailActivity::class.java)
            val gson = GsonBuilder().create()
            val jsonData = gson.toJson(myQueueResponse)
            intent.putExtra("data", jsonData)
            startActivityForResult(intent, Const.REQUEST_CODE_BOOK_QUEUE)
        }
    }
}
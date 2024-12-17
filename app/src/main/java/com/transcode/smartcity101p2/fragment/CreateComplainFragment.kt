package com.transcode.smartcity101p2.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.provider.MediaStore
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import java.io.IOException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.support.v4.content.ContextCompat
import android.util.Base64
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.core.exceptions.ServicesException
import com.mapbox.geojson.Point
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
import com.transcode.smartcity101p2.ComplainActivity
import com.transcode.smartcity101p2.CoreApplication
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.SelectPinLocationActivity
import com.transcode.smartcity101p2.adapter.ComplainTypeListAdapter
import com.transcode.smartcity101p2.adapter.ImageSelectedListAdapter
import com.transcode.smartcity101p2.contract.CreateComplainFragmentContract
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.dialog.SelectImageDialog
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.complain.ComplainTypeResponse
import com.transcode.smartcity101p2.presenter.CreateComplainFragmentPresenter
import kotlinx.android.synthetic.main.fragment_create_complain.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import kotlin.math.roundToInt

class CreateComplainFragment : CoreFragment(), CreateComplainFragmentContract.View, ComplainTypeListAdapter.ClickItem, ImageSelectedListAdapter.ClickDeleteItem, OnMapReadyCallback {
    override fun onClickDeleteItem(position: Int) {
        imageList.removeAt(position)
        imageAdapter.setData(imageList)
        imageAdapter.notifyDataSetChanged()

        add_image.visibility = View.VISIBLE
        if (imageList.size == 0) {
            recyclervieImage.visibility = View.GONE
        } else {
            recyclervieImage.visibility = View.VISIBLE
        }
    }

    override fun showError(message: String) {
        loadingDialog.dismiss()
        submit.isEnabled = true
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun updateCreateComplain(code: String) {
        loadingDialog.dismiss()
        submit.isEnabled = true
        clearData()
        if (code == "200" || code == "1") {
            context?.let {
                Toast.makeText(it, getString(R.string.complain_complain_send_success), Toast.LENGTH_LONG).show()

                adapter.setCurrentDataType(ComplainTypeResponse.ComplainTypeResponseData())
                text_details.setText("")
                text_name.setText("")
                text_tel.setText("")

                imageList = arrayListOf()
                imageAdapter.setData(imageList)
                imageAdapter.notifyDataSetChanged()

                add_image.visibility = View.VISIBLE
                if (imageList.size == 0) {
                    recyclervieImage.visibility = View.GONE
                } else {
                    recyclervieImage.visibility = View.VISIBLE
                }

                (it as ComplainActivity).moveToTab(1)
            }
        }
    }

    override fun onMapReady(map: MapboxMap) {
        mapboxMap = map
        context?.let {
            setUpMap(it)
        }
        map?.infoWindowAdapter = MyInfoWindowAdapter()
        map?.addOnMapClickListener {
            context?.let {
                startActivityForResult(Intent(it, SelectPinLocationActivity::class.java).apply {
                    putExtra("title", getString(R.string.complain_complain_event_location))
                }, 777)
            }
            false
        }

        context?.let {
            if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val latLng = if (isGrantedPermission()) {
                    getLocation()
                } else {
                    LatLng(16.054158, 103.652355)
                }
                click_latlng = latLng
                setMarker(latLng, getString(R.string.emergency_emergency_event_location))
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
                map?.moveCamera(cameraUpdate)
            } else {
//                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
//                        shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                    showLocationPermissionReasonable()
//                } else {
                ActivityCompat.requestPermissions(context as Activity,
                        arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 100)
//                }
            }
        }
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
                locationListener.onLocationChanged(null)
            }
        })

        val alert = alertBuilder.create()
        alert.show()
    }

    private fun showImagePermissionReasonable(requestCode: Int) {
        context?.let {
            val alertBuilder = AlertDialog.Builder(it)
            alertBuilder.setCancelable(false)
            alertBuilder.setTitle(R.string.permission_rationale_title)
            alertBuilder.setMessage("This app needs access device storage and camera to enable upload image")
            alertBuilder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface, which: Int) {
                    ActivityCompat.requestPermissions(context as Activity,
                            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA), requestCode)
                }
            })

            val alert = alertBuilder.create()
            alert.show()
        }
    }

    private fun setUpMap(context: Context) {
        mapboxMap?.setStyle(Style.Builder().fromUri(Const.MAP_STYLE_URI), Style.OnStyleLoaded {

        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                context?.let {
                    if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        val latLng = if (isGrantedPermission()) {
                            getLocation()
                        } else {
                            LatLng(16.054158, 103.652355)
                        }
                        click_latlng = latLng
                        setMarker(latLng, getString(R.string.emergency_emergency_event_location))
                        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
                        mapboxMap?.moveCamera(cameraUpdate)
                    } else {
                        locationListener.onLocationChanged(null)
                    }
                }
            }
            199 -> {
                context?.let {
                    if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(it, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        dispatchTakePictureIntent()
                    }
                }
            }
            200 -> {
                context?.let {
                    if (ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(it, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        dispatchGetPictureIntent()
                    }
                }
            }
        }
    }

    override fun onclickItem(res: ComplainTypeResponse.ComplainTypeResponseData) {
        adapter.setCurrentDataType(res)
    }

    override fun updateTypeList(list: ArrayList<ComplainTypeResponse.ComplainTypeResponseData>) {
        adapter.setData(list)
        adapter.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(mode: Int, title: String): CreateComplainFragment {
            return CreateComplainFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: CreateComplainFragmentPresenter
    lateinit var adapter: ComplainTypeListAdapter
    var selectedImgBase64 = ""
    private val CODE_GET_IMAGE = 500
    private val CODE_IMAGE_CAPTURE = 499
    private var mapboxMap: MapboxMap? = null
    private var first = true
    private var locationManager: LocationManager? = null
    private var click_latlng: LatLng? = null
    private var savedInstanceStates: Bundle? = null
    private var imageList = arrayListOf<String>()
    lateinit var imageAdapter: ImageSelectedListAdapter
    lateinit var loadingDialog: LoadingDialog
    private var stillLatLng: LatLng? = null

    var isAttached = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.savedInstanceStates = savedInstanceStates
        return inflater.inflate(R.layout.fragment_create_complain, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {
        context?.let {
            loadingDialog = LoadingDialog(it)
            loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog.setCancelable(false)
        }

        try {
            mapview.onCreate(savedInstanceStates)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapview.getMapAsync(this)

        presenter = CreateComplainFragmentPresenter(this)

        context?.let {
            adapter = ComplainTypeListAdapter(it)
            adapter.setRecyclerView(recyclerview)
            adapter.setClickListener(this)

            imageAdapter = ImageSelectedListAdapter(it)
            imageAdapter.setRecyclerView(recyclervieImage)
            imageAdapter.setDeleteClickListener(this)
        }

        presenter.getComplainType()

        submit.requestFocus()

        add_image.setOnClickListener {
            showSelectImageDialog()
        }

        pin_map.setOnClickListener {
            context?.let {
                startActivityForResult(Intent(it, SelectPinLocationActivity::class.java).apply {
                    putExtra("title", getString(R.string.complain_complain_event_location))
                }, 777)
            }
        }

        submit.setOnClickListener {

            val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            val isFB = user.authority_info?.IsFb
            if (isFB == "2") {
                showError(getString(R.string.need_login_text))
                return@setOnClickListener
            }

            if (adapter.getCurrentDataType().complain_type_id == null) {
                showError(getString(R.string.complain_complain_input_type))
                return@setOnClickListener
            }
            if (text_details.text.isNullOrEmpty()) {
                showError(getString(R.string.complain_complain_input_detail))
                return@setOnClickListener
            }
            if (text_name.text.isNullOrEmpty()) {
                showError(getString(R.string.complain_complain_input_name))
                return@setOnClickListener
            }
            if (text_tel.text.isNullOrEmpty()) {
                showError(getString(R.string.complain_complain_input_tel))
                return@setOnClickListener
            } else if (text_tel.text.length <= 9) {
                showError(getString(R.string.complain_complain_input_tel))
                return@setOnClickListener
            }

            if (location_text.text.toString() == "" || location_text.text.toString() == "null") {
                showError(getString(R.string.complain_complain_event_location))
                return@setOnClickListener
            }

            showLoadingDialog()
            submit.isEnabled = false

            val type = adapter.getCurrentDataType().complain_type_id.toString()
            val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            val city_id = login.authority_info?.CityId ?: "1"
            presenter.createComplain(text_details.text.toString(),
                    text_name.text.toString(),
                    click_latlng?.latitude.toString(),
                    click_latlng?.longitude.toString(),
                    "1",
                    type,
                    city_id,
                    text_tel.text.toString(),
                    "jpg",
                    location_text.text.toString(),
                    imageList)
        }

        context?.let {
            if (CoreApplication.getLanguage(it) == "zh") {
                val layoutParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParam.topMargin = it.resources.getDimensionPixelSize(R.dimen.dp10)
                concern_text.layoutParams = layoutParam
            } else if (CoreApplication.getLanguage(it) == "en") {
                concern_text.text = concern_text.text.toString().replace(" Sending", "\nSending")
            }
        }
    }


    private fun showSelectImageDialog() {
        context?.let {
            val selectImageDialog = SelectImageDialog(it)
            selectImageDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            selectImageDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
            selectImageDialog.show()
            selectImageDialog.setOnClickGalleryListener(View.OnClickListener {
                if (checkImagePermission()) {
                    dispatchGetPictureIntent()
                } else {
                    showImagePermissionReasonable(200)
                }
                selectImageDialog.dismiss()
            })
            selectImageDialog.setOnClickCaptureListener(View.OnClickListener {
                if (checkImagePermission()) {
                    dispatchTakePictureIntent()
                } else {
                    showImagePermissionReasonable(199)
                }
                selectImageDialog.dismiss()
            })
        }
    }

    private fun checkImagePermission(): Boolean {
        return context?.let {
            ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        } ?: false
    }

    private fun dispatchGetPictureIntent() {
        val photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, CODE_GET_IMAGE)
    }

    private fun dispatchTakePictureIntent() {
        context?.let {
            val co = it
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(co.packageManager)?.also {
                    startActivityForResult(takePictureIntent, CODE_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                777 -> {
                    val lat = data?.extras?.getDouble("lat") ?: 16.054158
                    val lng = data?.extras?.getDouble("lng") ?: 103.652355
                    setClickLatLng(lat, lng)
                    makeGeocodeSearch(LatLng(lat, lng))
                }
                CODE_GET_IMAGE -> {
                    val selectedImage = data?.data
                    selectedImgBase64 = try {
                        val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImage)

                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b = baos.toByteArray()
                        Base64.encodeToString(b, Base64.DEFAULT)
                    } catch (e: IOException) {
                        ""
                    }
                }
                CODE_IMAGE_CAPTURE -> {
                    selectedImgBase64 = try {
                        val bitmap = data?.extras?.get("data") as Bitmap

                        val baos = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b = baos.toByteArray()
                        Base64.encodeToString(b, Base64.DEFAULT)
                    } catch (e: IOException) {
                        ""
                    }
                }
            }
            if (requestCode == CODE_GET_IMAGE || requestCode == CODE_IMAGE_CAPTURE) {
                if (selectedImgBase64.isNotEmpty() && imageList.size < 3) {
                    imageList.add(selectedImgBase64)

                    imageAdapter.setData(imageList)
                    imageAdapter.notifyDataSetChanged()
                }
                if (imageList.size == 3) {
                    add_image.visibility = View.GONE
                } else {
                    add_image.visibility = View.VISIBLE
                }

                if (imageList.size == 0) {
                    recyclervieImage.visibility = View.GONE
                } else {
                    recyclervieImage.visibility = View.VISIBLE
                }
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

//        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10.0f, locationListener)
        val criteria = Criteria()
        criteria.accuracy = Criteria.ACCURACY_COARSE
        locationManager?.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)

        return locationManager?.getLastKnownLocation(locationManager?.getBestProvider(criteria, false))
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(p0: Location?) {
            if (!isAttached) {
                return
            }
            val latLng = LatLng(p0?.latitude ?: 16.054158, p0?.longitude ?: 103.652355)
            click_latlng = latLng
            mapboxMap?.let {
                if (first) {
                    for (mark in it.markers) {
                        it.removeMarker(mark)
                    }
                    first = false
                    val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
                    mapboxMap?.moveCamera(cameraUpdate)
                    setMarker(latLng, getString(R.string.complain_complain_event_location))

                    makeGeocodeSearch(latLng)
                    stillLatLng = latLng
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
                .icon(bitmapDescriptorFromVector(activity, R.mipmap.icon_pin_101))
        mapboxMap?.let {
            it.addMarker(marker)
            for (mark in it.markers) {
                if (mark.title == title) {
                    it.selectMarker(mark)
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

    private fun showLoadingDialog() {
        loadingDialog.show()
    }

    fun setClickLatLng(lat: Double, lng: Double) {
        click_latlng = LatLng(lat, lng)
        click_latlng?.let {
            val ll = it
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(it, 12.0)
            mapboxMap?.moveCamera(cameraUpdate)

            mapboxMap?.let {
                for (mark in it.markers) {
                    if (mark.title == getString(R.string.complain_complain_event_location)) it.removeMarker(mark)
                }
                setMarker(ll, getString(R.string.complain_complain_event_location))
            }
        }
    }

    override fun onAttach(context: Context?) {
        isAttached = true
        super.onAttach(context)
    }

    override fun onDetach() {
        isAttached = false
        super.onDetach()
    }

    private fun clearData() {
        text_details.setText("")
        text_name.setText("")
        text_tel.setText("")
        location_text.setText("")
        imageList = arrayListOf()
        imageAdapter.setData(imageList)
        imageAdapter.notifyDataSetChanged()

        stillLatLng?.let {
            setClickLatLng(it.latitude, it.longitude)
            makeGeocodeSearch(it)
        }
    }

    fun makeGeocodeSearch(latLng: LatLng) {
        try {
            val client = MapboxGeocoding.builder()
                    .accessToken(getString(R.string.mapbox_access_token))
                    .query(Point.fromLngLat(latLng.longitude, latLng.latitude))
                    .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
                    .mode(GeocodingCriteria.MODE_PLACES)
                    .build()
            client.enqueueCall(object : Callback<GeocodingResponse> {
                override fun onResponse(call: Call<GeocodingResponse>, response: Response<GeocodingResponse>) {
                    response.body()?.features()?.let {
                        if (it.size > 0) {
                            val feature = it[0]
                            val text = feature.placeName() ?: ""
                            location_text?.setText(text)
                        } else {
                            val locatios = latLng.latitude.toString() + " , " + latLng.longitude.toString()
                            location_text?.setText(locatios)
                        }
                    } ?: kotlin.run {
                        val locatios = latLng.latitude.toString() + " , " + latLng.longitude.toString()
                        location_text?.setText(locatios)
                    }

                }

                override fun onFailure(call: Call<GeocodingResponse>, throwable: Throwable) {}
            })
        } catch (servicesException: ServicesException) {
        }
    }
}
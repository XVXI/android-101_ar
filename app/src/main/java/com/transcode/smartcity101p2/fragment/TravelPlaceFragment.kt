package com.transcode.smartcity101p2.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.EditorInfo
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
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.R
import com.transcode.smartcity101p2.TravelPlaceDetailActivity
import com.transcode.smartcity101p2.adapter.AllProvinceAdapter
import com.transcode.smartcity101p2.adapter.AllSearchAdapter
import com.transcode.smartcity101p2.adapter.TextSuggestAdapter
import com.transcode.smartcity101p2.api.ApiRequest
import com.transcode.smartcity101p2.contract.TravelPlaceFragmentContract
import com.transcode.smartcity101p2.dialog.DialogSelectPlaceType
import com.transcode.smartcity101p2.extension.load
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.presenter.TravelPlaceFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout2
import kotlinx.android.synthetic.main.appbar_main2.view.*
import kotlinx.android.synthetic.main.fragment_policy.appbar
import kotlinx.android.synthetic.main.fragment_travel_place.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.roundToInt

class TravelPlaceFragment : CoreFragment(), OnMapReadyCallback, TravelPlaceFragmentContract.View, DialogSelectPlaceType.SelectTypeListener, AllProvinceAdapter.AllProvinceAdapterClickItem, TextSuggestAdapter.SuggestAdapterClickItem {

    val searchClick = object : AllSearchAdapter.AllSearchAdapterClickItem {
        override fun onAllSearchAdapterClickItem(place_name: String, place_id: String) {
            val intent = Intent(context, TravelPlaceDetailActivity::class.java)
            intent.putExtra("title", place_name)
            intent.putExtra("place_id", place_id)
            startActivity(intent)
        }
    }

    override fun updateSearchText(list: ArrayList<PlaceListByTypeResponse.PlaceMaker>) {
        mapboxMap?.let {
            for (mark in it.markers) {
                it.removeMarker(mark)
            }
        }

        if (list.isNotEmpty()) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng((list[0].Lat
                    ?: "0").toDouble(), (list[0].Lng ?: "0").toDouble()), 10.0)
            mapboxMap?.moveCamera(cameraUpdate)
        }

        for (data in list) {
            setMarker(LatLng((data.Lat ?: "0").toDouble(), (data.Lng
                    ?: "0").toDouble()), data.PlaceName ?: "")
        }

        mapboxMap?.setOnInfoWindowClickListener {
            for (data in list) {
                if (it.title == data.PlaceName) {
                    val intent = Intent(context, TravelPlaceDetailActivity::class.java)
                    intent.putExtra("title", data.PlaceName ?: "")
                    intent.putExtra("place_id", data.PlaceId ?: "")
                    startActivity(intent)
                    break
                }
            }
            true
        }


        sortList(list)
    }

    override fun onSuggestAdapterClickItem(res: String) {
        CURRENT_TYPE = TYPE_ALL
        button_selected_type.visibility = View.GONE
        enter = true

        edit_search.setText(res)
        button_close_search.performClick()
        presenter.placeSearchByText(res, city_id)
    }

    override fun updateText(list: ArrayList<PlaceListByTypeResponse.PlaceMaker>) {
        val slist = arrayListOf<String>()
        for (l in list) {
            slist.add(l.PlaceName ?: "")
        }
        if (list.size > 0) {
            layout_search_text.visibility = View.VISIBLE
        } else {
            layout_search_text.visibility = View.GONE
        }
        textSuggesstAdapter.setData(slist)
        textSuggesstAdapter.notifyDataSetChanged()
    }

    override fun onSelectType(type: String, url: String) {
        enter = true
        edit_search.setText("")
        CURRENT_TYPE = type
        typeUrl = url
        checkType(url)
    }

    companion object {
        fun newInstance(mode: Int, title: String): TravelPlaceFragment {
            return TravelPlaceFragment().apply {
                val bundle = Bundle()
                bundle.putInt("MODE", mode)
                bundle.putString("TITLE", title)
                arguments = bundle
            }
        }
    }

    private val TYPE_ALL = "0"
    private var city_name = ""
    private var city_id = ""
    private var savedInstanceState: Bundle? = null
    private var mapboxMap: MapboxMap? = null
    private var locationManager: LocationManager? = null

    private var CURRENT_TYPE = TYPE_ALL
    private var typeUrl = ""

    lateinit var presenter: TravelPlaceFragmentPresenter
    lateinit var allProvinceAdapter: AllProvinceAdapter
    lateinit var textSuggesstAdapter: TextSuggestAdapter
    var allSearchAdapter: AllSearchAdapter? = null
    var placetype = arrayListOf<PlaceTypeResponse.PlaceTypeResponseData>()

    var enter = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        this.savedInstanceState = savedInstanceState
        return inflater.inflate(R.layout.fragment_travel_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
    }

    private fun initView() {

        presenter = TravelPlaceFragmentPresenter(this)

        context?.let {
            allProvinceAdapter = AllProvinceAdapter(it)
            allProvinceAdapter.setRecyclerView(all_province)
            allProvinceAdapter.setClickListener(this)

            textSuggesstAdapter = TextSuggestAdapter(it)
            textSuggesstAdapter.setRecyclerView(recycler_text)
            textSuggesstAdapter.setClickListener(this)
        }

        val appBar = appbar as CustomAppBarLayout2
        appBar.setTitle(arguments?.getString("TITLE") ?: "")
        appBar.setTitleColor(resources.getColor(R.color.dark_purple))
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }
        appBar.rightBt0.visibility = View.GONE
        appBar.rightBt0.load("", R.mipmap.icon_qrs)

        appBar.rightBt.visibility = View.VISIBLE
        if (mapView.isShown) {
            appBar.rightBt.load("", R.mipmap.icon_list_state)
        } else {
            appBar.rightBt.load("", R.mipmap.icon_map_state)
        }

        appBar.rightBt.setOnClickListener {
            if (mapView.isShown) {
                appBar.rightBt.load("", R.mipmap.icon_map_state)
                mapView.visibility = View.GONE
                layout_type.visibility = View.GONE
                layout_list.visibility = View.VISIBLE
            } else {
                appBar.rightBt.load("", R.mipmap.icon_list_state)
                mapView.visibility = View.VISIBLE
                layout_type.visibility = View.VISIBLE
                layout_list.visibility = View.GONE
            }
        }

        val loginResponse = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        loginResponse?.let {
            city_name = it.authority_info?.CityName.toString()
            city_id = it.authority_info?.CityId.toString()
        }

        try {
            mapView.onCreate(savedInstanceState)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mapView.getMapAsync(this)

        button_province.setOnClickListener {
            layout_province.visibility = View.VISIBLE
            layout_search_text.visibility = View.GONE

            val list = Hawk.get<ArrayList<ProvinceResponse.ProvinceResponseData>>("PROVINCE_LIST")
                    ?: arrayListOf()

            if (list.isNotEmpty()) {
                updateProvince(list)
            } else {
                presenter.getProvince()
            }
        }

        button_close_province.setOnClickListener {
            layout_province.visibility = View.GONE
            hideSoftKeyboard()
        }

//        edit_search.setOnClickListener {
//            layout_search_text.visibility = View.VISIBLE
//            layout_province.visibility = View.GONE
//        }

        button_close_search.setOnClickListener {
            layout_search_text.visibility = View.GONE
            hideSoftKeyboard()
        }

        button_select_type.setOnClickListener {
            showSelectTypeDialog()
        }

        button_all_type.setOnClickListener {
            CURRENT_TYPE = TYPE_ALL
            checkType("")
        }

        edit_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    if (it.toString().isEmpty()) {
                        if (!enter) {
                            checkType("")
                        }
                    } else {
                        if (!enter) {
                            presenter.placeSearchByTypeTextOnly(it.toString(), city_id)
                        }
                    }
                }
                enter = false
            }
        })

        edit_search.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //search
                if (textSuggesstAdapter.list.isNotEmpty()) {
                    onSuggestAdapterClickItem(textSuggesstAdapter.list[0])
                }
                true
            } else {
                false
            }
        }
    }

    override fun onMapReady(map: MapboxMap) {
        map.setStyle(Style.Builder().fromUri(Const.MAP_STYLE_URI), Style.OnStyleLoaded {

        })
        mapboxMap = map

        mapboxMap?.uiSettings?.setCompassMargins(0, layout_search.height + resources.getDimensionPixelSize(R.dimen.dp10), 0, 0)

        val latLng = if (isGrantedPermission()) {
            getLocation()
        } else {
            LatLng(16.054158, 103.652355)
        }
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 12.0)
        map?.moveCamera(cameraUpdate)
        map?.infoWindowAdapter = MyInfoWindowAdapter()

        checkType("")
    }

    internal inner class MyInfoWindowAdapter : MapboxMap.InfoWindowAdapter {
        override fun getInfoWindow(marker: Marker): View? {
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
        locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

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
            mapboxMap?.let {}
        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

        }

        override fun onProviderEnabled(p0: String?) {

        }

        override fun onProviderDisabled(p0: String?) {

        }
    }

    private fun setMarker(lngYou: LatLng, title: String) {
        context?.let {
            val marker = MarkerOptions()
                    .title(title)
                    .position(LatLng(lngYou.latitude, lngYou.longitude))
                    .icon(bitmapDescriptorFromVector(it, R.mipmap.icon_pin_101))
            mapboxMap?.let {
                it.addMarker(marker)
            }
        }
    }

    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): Icon {
        val w = (100 * 0.8).roundToInt()
        val h = (120 * 0.8).roundToInt()

        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable?.setBounds(0, 0, w, h)
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val resizeBitmap = Bitmap.createScaledBitmap(bitmap, w, h, false)
        val canvas = Canvas(resizeBitmap)
        vectorDrawable?.draw(canvas)
        return IconFactory.getInstance(context).fromBitmap(resizeBitmap)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        allSearchAdapter?.notifyDataSetChanged()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        locationManager?.removeUpdates(locationListener)
    }

    override fun updateMaker(list: ArrayList<PlaceListByTypeResponse.PlaceMaker>) {
        mapboxMap?.let {
            for (mark in it.markers) {
                it.removeMarker(mark)
            }
        }

        if (list.isNotEmpty()) {
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(LatLng((list[0].Lat
                    ?: "0").toDouble(), (list[0].Lng ?: "0").toDouble()), 10.0)
            mapboxMap?.moveCamera(cameraUpdate)
        }

        for (data in list) {
            setMarker(LatLng((data.Lat ?: "0").toDouble(), (data.Lng
                    ?: "0").toDouble()), data.PlaceName ?: "")
        }

        mapboxMap?.setOnInfoWindowClickListener {
            for (data in list) {
                if (it.title == data.PlaceName) {
                    val intent = Intent(context, TravelPlaceDetailActivity::class.java)
                    intent.putExtra("title", data.PlaceName ?: "")
                    intent.putExtra("place_id", data.PlaceId ?: "")
                    startActivity(intent)
                    break
                }
            }
            true
        }


        sortList(list)
    }

    private fun getPlaceType(city_id: String) {
        val callbacks = object : Callback<PlaceTypeResponse> {
            override fun onResponse(call: Call<PlaceTypeResponse>?, response: Response<PlaceTypeResponse>?) {
                response?.body()?.data?.let {
                    placetype = it
                    presenter.placeSearchByType(CURRENT_TYPE, city_id)
                }
            }

            override fun onFailure(call: Call<PlaceTypeResponse>?, t: Throwable?) {

            }
        }
        ApiRequest.INSTANCE.requestPlaceType(callbacks, city_id)
    }

    private fun getPlaceSuggestion(city_id: String, l: ArrayList<HashMap<String, Any>>) {
        val callbacks = object : Callback<PlaceSuggestionResponse> {
            override fun onResponse(call: Call<PlaceSuggestionResponse>?, response: Response<PlaceSuggestionResponse>?) {
                response?.body()?.data?.let {
                    if (it.size > 0) {

                        val h = hashMapOf<String, Any>()
                        h["type"] = "0"
                        h["type_name"] = "Suggestion"
                        h["data"] = it

                        l.add(0, h)

                        context?.let {
                            allSearchAdapter = AllSearchAdapter(it)
                            allSearchAdapter?.setRecyclerView(recycler_search_all)
                            allSearchAdapter?.setClickListener(searchClick)
                            allSearchAdapter?.setData(l)
                            allProvinceAdapter.notifyDataSetChanged()
                        }
                    } else {
                        onFailure(null, null)
                    }
                } ?: kotlin.run {
                    onFailure(null, null)
                }
            }

            override fun onFailure(call: Call<PlaceSuggestionResponse>?, t: Throwable?) {
                context?.let {
                    allSearchAdapter = AllSearchAdapter(it)
                    allSearchAdapter?.setRecyclerView(recycler_search_all)
                    allSearchAdapter?.setClickListener(searchClick)
                    allSearchAdapter?.setData(l)
                    allProvinceAdapter.notifyDataSetChanged()
                }
            }
        }
        ApiRequest.INSTANCE.requestPlaceSuggestion(callbacks, city_id)
    }

    private fun sortList(list: ArrayList<PlaceListByTypeResponse.PlaceMaker>) {
        val l = arrayListOf<HashMap<String, Any>>()
        val type = arrayListOf<String>()
        for (t in list) {
            var inList = false
            for (ti in type) {
                if (ti == t.Type) {
                    inList = true
                }
            }
            if (!inList) {
                type.add(t.Type.toString())
            }
        }

        for (t in type) {
            val datalist = arrayListOf<PlaceListByTypeResponse.PlaceMaker>()
            for (data in list) {
                if (t == data.Type) {
                    datalist.add(data)
                }
            }
            if (datalist.size > 0) {
                val h = hashMapOf<String, Any>()
                h["type"] = t
                h["type_name"] = "type_name"
                for (tn in placetype) {
                    if (t == tn.type_id) {
                        h["type_name"] = tn.type_name ?: ""
                    }
                }
                h["data"] = datalist
                l.add(h)
            }
        }

//        context?.let {
//            allSearchAdapter = AllSearchAdapter(it)
//            allSearchAdapter.setRecyclerView(recycler_search_all)
//            allSearchAdapter.setData(l)
//            allProvinceAdapter.notifyDataSetChanged()
//        }
//        getSuggestion

        if (edit_search.text.isNullOrEmpty()) {
            getPlaceSuggestion(city_id, l)
        } else {
            context?.let {
                allSearchAdapter = AllSearchAdapter(it)
                allSearchAdapter?.setRecyclerView(recycler_search_all)
                allSearchAdapter?.setClickListener(searchClick)
                allSearchAdapter?.setData(l)
                allProvinceAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun checkType(url: String) {
        if (CURRENT_TYPE == TYPE_ALL) {
            button_selected_type.visibility = View.GONE
        } else {
            button_selected_type.visibility = View.VISIBLE

            button_selected_type.load(url, R.mipmap.icon_all_inactive)
        }

        getPlaceType(city_id)
    }

    private fun showSelectTypeDialog() {
        context?.let {
            val dialogSelectPlaceType = DialogSelectPlaceType(it)
            dialogSelectPlaceType.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogSelectPlaceType.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialogSelectPlaceType.setTypeListener(this)
            dialogSelectPlaceType.show()
        }
    }

    override fun updateProvince(data: ArrayList<ProvinceResponse.ProvinceResponseData>) {
        Hawk.delete("PROVINCE_LIST")
        Hawk.put("PROVINCE_LIST", data)

        allProvinceAdapter.setData(data)
        allProvinceAdapter.notifyDataSetChanged()
    }

    override fun onAllProvinceAdapterClickItem(res: ProvinceResponse.ProvinceResponseData) {
        city_id = res.province_id ?: "1"
        button_close_province.performClick()
        if (CURRENT_TYPE == TYPE_ALL) {
            checkType("")
        } else {
            checkType(typeUrl)
        }
    }
}
package com.transcode.smartcity101p2.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.adapter.PhotosAdapter
import com.transcode.smartcity101p2.contract.QueueDetailFragmentContract
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.presenter.QueueDetailFragmentPresenter
import com.transcode.smartcity101p2.view.CustomAppBarLayout
import kotlinx.android.synthetic.main.appbar_main.view.*
import kotlinx.android.synthetic.main.fragment_queue_detail.*
import android.content.ActivityNotFoundException
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.text.Html
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.*
import com.transcode.smartcity101p2.model.*


class QueueDetailFragment : CoreFragment(), QueueDetailFragmentContract.View {

    companion object {
        fun newInstance(queueListResponse: QueueListResponse): QueueDetailFragment {
            return QueueDetailFragment().apply {
                val gson = GsonBuilder().create()
                val queueListResponseString = gson.toJson(queueListResponse)
                val bundle = Bundle()
                bundle.putString("data", queueListResponseString)
                arguments = bundle
            }
        }
    }

    lateinit var presenter: QueueDetailFragmentPresenter
    private var queueListResponse: QueueListResponse = QueueListResponse()
    var loadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_queue_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val bundle = savedInstanceState ?: arguments

        bundle?.let {
            if (it.containsKey("data")) {
                val gson = Gson()
                queueListResponse = gson.fromJson(it.getString("data"), QueueListResponse::class.java)
            }
        }

        grantLocationPermission()
        initView()
    }

    private fun initView() {
        val appBar = appbar as CustomAppBarLayout
        appBar.setTitle(queueListResponse.QueueName.toString())
        appBar.leftBt.setOnClickListener {
            activity?.apply {
                finish()
            }
        }

        context?.let {
            loadingDialog = LoadingDialog(it)
            loadingDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            loadingDialog?.setCancelable(false)
        }

        presenter = QueueDetailFragmentPresenter(this)

        loadingDialog?.show()
        presenter.getQueueDetail(queueListResponse.QueueId ?: "")
    }

    override fun updateView(queueDetailResponse: QueueDetailResponse) {

        loadingDialog?.dismiss()

        if (photos_viewpager == null) {
            return
        }

        queueDetailResponse.QueueFile?.let {
            layout_file?.visibility = View.VISIBLE

            var file = queueDetailResponse.QueueFile ?: arrayListOf()
            if (file.size > 0) {
                for (o in file) {
                    val v = TextView(context)
                    v.setTextColor(Color.parseColor("#000000"))
                    layout_filename.addView(v)
                    var filename = o.FileURL ?: ""
                    if (filename.contains('/')) {
                        filename = filename.substring(filename.lastIndexOf('/') + 1)
                    }
                    v.text = filename
                    val pl = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    pl.setMargins(20, 0, 20, 0)
                    v.layoutParams = pl
                    v.setOnClickListener {
                        val target = Intent(Intent.ACTION_VIEW)
                        target.setDataAndType(Uri.parse(o.FileURL), "application/pdf")
                        target.flags = Intent.FLAG_ACTIVITY_NO_HISTORY

                        val intent = Intent.createChooser(target, "Open File")
                        try {
                            startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            // Instruct the user to install a PDF reader here, or something
                        }

                    }
                }
            }

        } ?: kotlin.run {
            layout_file?.visibility = View.GONE
        }

        text_detail?.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(queueDetailResponse.QueueDetail, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(queueDetailResponse.QueueDetail)
        }

        val lat = queueDetailResponse.QueueLat ?: "0"
        val lng = queueDetailResponse.QueueLng ?: "0"
        if (lat == "0" && lng == "0") {
            layout_location?.visibility = View.GONE
        } else {
            layout_location?.visibility = View.VISIBLE
        }

        location?.setOnClickListener {
            //call map fragment
            activity?.apply {
                val intent = Intent(context, LocationActivity::class.java)
                intent.putExtra("lat", lat.toDouble())
                intent.putExtra("lng", lng.toDouble())
                startActivity(intent)
            }
        }
        booking?.setOnClickListener {
            val account = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)

            if (account.authority_info?.IsFb == "2") {
                context?.let {
                    val customDialog = CustomDialog(it)
                    customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                    customDialog.show()
                    customDialog.setTitle(it.getString(R.string.appplication_name))
                    customDialog.setMessage(getString(R.string.need_login_text))
                    customDialog.setOnClickOKListener(View.OnClickListener {
                        customDialog.dismiss()
                        Hawk.delete(Const.KEY_LOGIN_DATA)
                        activity?.finish()
                        val intent = Intent(activity, LoginActivity::class.java)
                        intent.putExtra(Const.KEY_NEED_LOGIN, true)
                        activity?.startActivity(intent)
                    })
                    customDialog.setOnClickCancelListener(View.OnClickListener { customDialog.dismiss() })
                }
                return@setOnClickListener
            }
            if (queueDetailResponse.QueueType == "1") {
                if (isGrantedPermission()) {
//                    val lat = (queueDetailResponse.QueueLat ?: "0").toDouble()
//                    val lan = (queueDetailResponse.QueueLng ?: "0").toDouble()
//                    val currentLocation = getCurrentLocation()
//                    if (distance(lat, lan, currentLocation.latitude, currentLocation.longitude, "K") > 5) {
//                        context?.let {
//                            Toast.makeText(it, "you location far from 5km", Toast.LENGTH_LONG).show()
//                        }
//                    } else {
                    context?.let {
                        if (queueDetailResponse.FormURL != null) {
                            //open form url
                            activity?.apply {
                                val intent = Intent(this, WebFormActivity::class.java)
                                intent.putExtra("title", "Form")
                                intent.putExtra("url", queueDetailResponse.FormURL ?: "")
                                intent.putExtra("queue_type", queueDetailResponse.QueueType ?: "1")
                                intent.putExtra("queue_id", queueDetailResponse.QueueId ?: "1")
                                intent.putExtra("queue_title", queueDetailResponse.QueueName ?: "")
                                startActivityForResult(intent, Const.REQUEST_CODE_BOOK_QUEUE)
                            }
                        } else {
                            val customDialog = CustomDialog(it)
                            customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                            customDialog.show()
                            customDialog.setTitle(getString(R.string.confirm_queue_title_text))
                            customDialog.setMessage(getString(R.string.confirm_queue_message_text))
                            customDialog.setOnClickOKListener(View.OnClickListener {
                                customDialog.dismiss()
                                presenter.bookNow(queueDetailResponse.QueueId.toString(), account.authority_info?.CitizenId.toString())
                            })
                            customDialog.setOnClickCancelListener(View.OnClickListener { customDialog.dismiss() })
                        }
                    }
//                    }
                }
            } else {
                activity?.apply {
                    val queue_id = queueDetailResponse.QueueId ?: "1"
                    val queue_type = queueDetailResponse.QueueType ?: "2"
                    val form_url = queueDetailResponse?.FormURL ?: ""
                    val base_lat = queueDetailResponse?.QueueLat ?: "0"
                    val base_lng = queueDetailResponse?.QueueLng ?: "0"
                    val queue_title = queueDetailResponse?.QueueName ?: ""
                    val intent = Intent(this, SelectDateActivity::class.java)
                    intent.putExtra("queue_id", queue_id)
                    intent.putExtra("queue_type", queue_type)
                    intent.putExtra("form_url", form_url)
                    intent.putExtra("base_lat", base_lat)
                    intent.putExtra("base_lng", base_lng)
                    intent.putExtra("queue_title", queue_title)
                    intent.putExtra("isEdit", false)
                    startActivityForResult(intent, Const.REQUEST_CODE_BOOK_QUEUE)
                }
            }
        }

        context?.let {
            val photoAdapter = PhotosAdapter(it)
            photos_viewpager.adapter = photoAdapter
            queueDetailResponse.QueueImg?.let {
                val list = arrayListOf<String>()
                for (n in it) {
                    list.add(n.ImgURL ?: "")
                }
                photoAdapter.setData(list)
                photoAdapter.notifyDataSetChanged()
                tab_layout.setupWithViewPager(photos_viewpager, true)
                photos_viewpager.setCurrentItem(0, false)
            }

            for (i in 0 until photoAdapter.count step 1) {
                val tab = LayoutInflater.from(context).inflate(R.layout.custom_tab2, null)
                tab.setBackgroundResource(R.drawable.tab_selector)
                tab_layout.getTabAt(i)?.customView = tab
                tab_layout.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(it.resources.getDimensionPixelSize(R.dimen.dimen_tab_width), LinearLayout.LayoutParams.MATCH_PARENT)
            }
            photos_viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

                }

                override fun onPageSelected(position: Int) {
                    if (position >= photoAdapter.count) {
                        photos_viewpager?.setCurrentItem(0, false)
                    } else {
                        photos_viewpager?.setCurrentItem(position, true)
                    }
                }

            })
        }

        autoScroll()
    }

    private fun getCurrentLocation(): Location {
        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        @SuppressLint("MissingPermission") val location = locationManager.getLastKnownLocation(locationManager
                .getBestProvider(criteria, false))
        return location
    }

    private fun grantLocationPermission() {
        activity?.let {
            if (!isGrantedPermission()) {
                ActivityCompat.requestPermissions(it, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
            }
        }
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts decimal degrees to radians						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    /*::	This function converts radians to decimal degrees						 :*/
    /*:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*/
    private fun rad2deg(rad: Double): Double {
        return rad * 180 / Math.PI
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, unit: String): Double {
        val theta = lon1 - lon2
        var dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist *= 60.0 * 1.1515
        if (unit === "K") {
            dist *= 1.609344
        } else if (unit === "N") {
            dist *= 0.8684
        }

        return dist
    }

    override fun showError(message: String) {
        loadingDialog?.dismiss()
        context?.let {
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
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

    private val handler = Handler()
    private val autoScrollRunnable = Runnable {
        photos_viewpager?.let {
            val nextItem = if (it.currentItem + 1 < it.adapter?.count ?: 0) {
                it.currentItem + 1
            } else {
                0
            }
            it.setCurrentItem(nextItem, true)
            autoScroll()
        }
    }

    fun autoScroll() {
        handler.removeCallbacks(autoScrollRunnable)
        handler.postDelayed(autoScrollRunnable, 2500)
    }
}
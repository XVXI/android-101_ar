package com.transcode.smartcity101p2

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.support.v4.app.ActivityCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v4.widget.DrawerLayout
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.google.gson.GsonBuilder
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.adapter.NewsPaggerAdapter
import com.transcode.smartcity101p2.adapter.NotificationListAdapter
import com.transcode.smartcity101p2.contract.HomeActivityContract
import com.transcode.smartcity101p2.dialog.SelectEditDialog
import com.transcode.smartcity101p2.extension.loadCircle
import com.transcode.smartcity101p2.firebase.FirebaseMessagingManager
import com.transcode.smartcity101p2.firebase.MyFirebaseMessagingService
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.fragment.HomeFragment
import com.transcode.smartcity101p2.presenter.HomeActivityPresenter
import com.transcode.smartcity101p2.utils.AppUtils
import com.transcode.smartcity101p2.ar.PermissionUtil
import com.transcode.smartcity101p2.ar.SampleData
import com.transcode.smartcity101p2.ar.SimpleArActivity
import com.transcode.smartcity101p2.dialog.CustomDialog
import com.transcode.smartcity101p2.model.*
import com.transcode.smartcity101p2.services.UpdateNotification
import com.wikitude.architect.ArchitectView
import com.wikitude.common.camera.CameraSettings
import com.wikitude.common.devicesupport.Feature
import com.wikitude.common.permission.PermissionManager
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.view_notification.view.*
import kotlinx.android.synthetic.main.view_account.view.*
import org.json.JSONObject
import java.io.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class HomeActivity : CoreActivity(), HomeActivityContract.View, NewsPaggerAdapter.ClickItem, NotificationListAdapter.ClickItem {
    override fun onclickItem(res: Any) {
        if (res is MyQueueResponse) {
            val intent = Intent(this, TicketDetailActivity::class.java)
            val gson = GsonBuilder().create()
            val jsonData = gson.toJson(res)
            intent.putExtra("data", jsonData)
            startActivity(intent)
        } else if (res is HashMap<*, *>) {
            if (res["type"].toString() == "emergency") {
                checkLocationPermission(Intent(this, EmergencyActivity::class.java).apply {
                    putExtra("list", true)
                })
            } else if (res["type"].toString() == "complain") {
                checkLocationPermission(Intent(this, ComplainActivity::class.java).apply {
                    putExtra("list", true)
                })
            }
        }

        drawerLayout.closeDrawer(nav_view_right)
    }

    companion object {
        private var _instance: HomeActivity? = null
        fun getInstance(): HomeActivity? {
            return _instance
        }
    }

    private val handler = Handler()
    private lateinit var presenter: HomeActivityPresenter
    private lateinit var newsPaggerAdapter: NewsPaggerAdapter
    private var imagecount = 0
    private var newsList = arrayListOf<NewsResponse>()
    private lateinit var notification_view: View
    private lateinit var account_view: View
    private var back_pressed = 0L
    lateinit var notificationList: ArrayList<Any>
    lateinit var notificationAdapter: NotificationListAdapter
    private val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

    private val receiver = HomeReceiver()

    val REQ_STORAGE_PERMISSION = 1234
    val REQ_HELP_PERMISSION = 1235

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        _instance = this
        FirebaseMessagingManager().subscribeTopics()
        FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, HomeFragment.newInstance(), R.id.content_home_frame)

        val filter = IntentFilter(Const.BROADCAST_TOGGLE_NOTIFICATION)
        this.registerReceiver(receiver, filter)

        if (intent.extras != null) {
            checkLayoutReady()
        }

        text_menu1.isSelected = true
        text_menu2.isSelected = true
        text_menu3.isSelected = true
        text_menu4.isSelected = true

        val vi = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        notification_view = vi.inflate(R.layout.view_notification, null)
        notification_view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        account_view = vi.inflate(R.layout.view_account, null)
        account_view.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

        val policys = getString(R.string.account_text_setting) + " & \n" + getString(R.string.setting_setting_privacy0)
        account_view.layout_edit_text.text = policys

        account_view.layout_logout.setOnClickListener {
            logOut()
        }

        account_view.market_profile_logout.setOnClickListener {
            logOut()
        }

        account_view.layout_about.setOnClickListener {
            //            hideAccountView()
            startActivity(Intent(this, AboutActivity::class.java))
        }

        account_view.market_profile_about.setOnClickListener {
            drawerLayout.closeDrawer(nav_view)
            startActivity(Intent(this, AboutActivity::class.java))
        }

        account_view.layout_edit.setOnClickListener {
            //            hideAccountView()
//            showEditDialog()
//            startActivity(Intent(this, EditAccountActivity::class.java))
            startActivity(Intent(this, SettingActivity::class.java))
            drawerLayout.closeDrawer(nav_view)
        }

        account_view.market_profile_info.setOnClickListener {
            startActivity(Intent(this, InfoActivity::class.java))
            drawerLayout.closeDrawer(nav_view)
        }

        account_view.layout_privacy.setOnClickListener {
            val prefix = when {
                CoreApplication.getLanguage(this).equals("th", true) -> "/policy/th"
                CoreApplication.getLanguage(this).equals("en", true) -> "/policy/eng"
                else -> "/policy/chi"
            }

            val market_url = BuildConfig.MARKETS_URL
            val url = "$market_url/$prefix"
            val webIntent = Intent(Intent.ACTION_VIEW)
            webIntent.data = Uri.parse(url)
            startActivity(webIntent)
        }

        if (checkNewNotification()) {
            notification_new.visibility = View.VISIBLE
            notification_toggle_new.visibility = View.VISIBLE
        } else {
            notification_new.visibility = View.GONE
            notification_toggle_new.visibility = View.GONE
        }

        notificationAdapter = NotificationListAdapter(this)
        notificationAdapter.setRecyclerView(notification_view.recyclerview_notification)
        notificationAdapter.setClickListener(this)

        button_notification.setOnClickListener {
            drawerLayout.openDrawer(nav_view_right)
            showNotificationView()
        }
        button_account.setOnClickListener {
            drawerLayout.openDrawer(nav_view)
            showAccountView()
        }

        button_news.setOnClickListener {
            startActivity(Intent(this, NewsListActivity::class.java))
        }

        newsPaggerAdapter = NewsPaggerAdapter(this)
        news_viewpager.adapter = newsPaggerAdapter

        presenter = HomeActivityPresenter(this)
        showLoading()
        presenter.getNews(null, 5, 0)

        button_call_phone.setOnClickListener {
            startActivity(Intent(this, CallPhoneActivity::class.java))
        }

        button_complain.setOnClickListener {
            checkLocationPermission(Intent(this, ComplainActivity::class.java))
        }

        button_emergency.setOnClickListener {
            checkLocationPermission(Intent(this, EmergencyActivity::class.java))
        }

        button_cctv_menu.setOnClickListener {
            checkLocationPermission(Intent(this, CCtvActivity::class.java))
        }

        button_cctv_menu_l.setOnClickListener {
            button_cctv_menu.performClick()
        }

        button_youtube.setOnClickListener {
            startActivity(Intent(this, YoutubeListActivity::class.java))
        }

        button_queue.setOnClickListener {
            startActivity(Intent(this, QueueActivity::class.java))
        }

        button_article.setOnClickListener {
            val intent = Intent(this, NewsListActivity::class.java)
            val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            val cat_id = login.authority_info?.ArticleNewsCatId.toString()
            intent.putExtra("title", getString(R.string.home_article))
            intent.putExtra("cat_id", cat_id)
            startActivity(intent)
        }

        button_reward.setOnClickListener {
            val intent = Intent(this, NewsListActivity::class.java)
            val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            val cat_id = login.authority_info?.AwardNewsCatId.toString()
            intent.putExtra("title", getString(R.string.home_reward))
            intent.putExtra("cat_id", cat_id)
            startActivity(intent)
        }

        button_travel.setOnClickListener {
            val intent = Intent(this, TravelHomeActivity::class.java)
            checkLocationPermission(intent)
        }

        button_market.setOnClickListener {
            val intent = Intent(this, MarketHomeActivity::class.java)
            startActivity(intent)
        }

        layout_account_root.addView(account_view)
        layout_notification_root.addView(notification_view)

        account_toggle.setOnClickListener {
            drawerLayout.openDrawer(nav_view)
        }

        button_ar.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                copyVideo()
                openAR()
            } else {
                showARPermissionReasonable()
            }
        }

        button_pdf.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openHelp()
            } else {
                showHelpPermissionReasonable()
            }
        }

        button_ss.setOnClickListener {
            showSSPopup()
        }

        button_store.setOnClickListener {
            val intent = Intent(this, WebForm2Activity::class.java)
            startActivity(intent)
        }

        notification_toggle.setOnClickListener {
            drawerLayout.openDrawer(nav_view_right)
        }

        button_notification.visibility = View.GONE
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, nav_view_right)

        drawerLayout.setDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerStateChanged(p0: Int) {

            }

            override fun onDrawerSlide(p0: View, p1: Float) {

            }

            override fun onDrawerClosed(p0: View) {

            }

            override fun onDrawerOpened(view: View) {
                if (view.id == nav_view.id) {
                    showAccountView()
                } else {
                    showNotificationView()
                }
            }
        })

        account_view.layout_slide_menu_account.setOnClickListener {
            drawerLayout.closeDrawer(nav_view)
        }

        notification_view.notification_root.setOnClickListener {
            drawerLayout.closeDrawer(nav_view_right)
        }

        intent.extras?.let { ext ->
            val type = ext.getString("type")

            when (type) {
                "emergency" -> checkLocationPermission(Intent(this, EmergencyActivity::class.java).apply {
                    val emer_id = try {
                        val jsonString = ext.getString("extra_data")
                        val json = JSONObject(jsonString)
                        json.getString("emer_id")
                    } catch (e: Exception) {
                        ""
                    }

                    putExtra("type", "emergency")
                    putExtra("emer_id", emer_id)
                })
                "complain" -> checkLocationPermission(Intent(this, ComplainActivity::class.java).apply {
                    val complain_id = try {
                        val jsonString = ext.getString("extra_data")
                        val json = JSONObject(jsonString)
                        json.getString("complain_id")
                    } catch (e: Exception) {
                        ""
                    }

                    putExtra("type", "complain")
                    putExtra("complain_id", complain_id)
                })
                "emer_chat" -> checkLocationPermission(Intent(this, EmergencyActivity::class.java).apply {
                    val emer_id = try {
                        val jsonString = ext.getString("extra_data")
                        val json = JSONObject(jsonString)
                        json.getString("emer_id")
                    } catch (e: Exception) {
                        ""
                    }
                    putExtra("type", "emer_chat")
                    putExtra("emer_id", emer_id)
                })
                "complain_chat" -> checkLocationPermission(Intent(this, ComplainActivity::class.java).apply {
                    val complain_id = try {
                        val jsonString = ext.getString("extra_data")
                        val json = JSONObject(jsonString)
                        json.getString("complain_id")
                    } catch (e: Exception) {
                        ""
                    }
                    putExtra("type", "complain_chat")
                    putExtra("complain_id", complain_id)
                })
            }
        }

        val max_pad = resources.getDimensionPixelSize(R.dimen.dp10) * 6

        text_menu1.maxWidth = ((CoreApplication.getScreenWidth() - max_pad) / 3) - resources.getDimensionPixelSize(R.dimen.dp17)
        text_menu2.maxWidth = ((CoreApplication.getScreenWidth() - max_pad) / 3) - resources.getDimensionPixelSize(R.dimen.dp17)
    }

    private fun showHelpPermissionReasonable() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle(R.string.permission_rationale_title)
        alertBuilder.setMessage("This app needs access device storage to enable 'Help'")
        alertBuilder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) &&
                            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) &&
                            shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        ActivityCompat.requestPermissions(this@HomeActivity, arrayOf(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ), REQ_HELP_PERMISSION)
                    } else {
                        showHelpPermissionReasonable2()
                    }
                } else {
                    ActivityCompat.requestPermissions(this@HomeActivity, arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), REQ_HELP_PERMISSION)
                }
            }
        })
        alertBuilder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

            }
        })

        val alert = alertBuilder.create()
        alert.show()
    }

    private fun showHelpPermissionReasonable2() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle(R.string.permission_rationale_title)
        alertBuilder.setMessage("This app needs access device storage to enable 'Help' Visit Settings and Allow permissions")
        alertBuilder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        })
        alertBuilder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

            }
        })

        val alert = alertBuilder.create()
        alert.show()
    }

    private fun showARPermissionReasonable() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle(R.string.permission_rationale_title)
        alertBuilder.setMessage("This app needs access device storage and camera to enable 'AR'")
        alertBuilder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) &&
                            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) &&
                            shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        ActivityCompat.requestPermissions(this@HomeActivity, arrayOf(
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.CAMERA
                        ), REQ_STORAGE_PERMISSION)
                    } else {
                        showARPermissionReasonable2()
                    }
                } else {
                    ActivityCompat.requestPermissions(this@HomeActivity, arrayOf(
                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA
                    ), REQ_STORAGE_PERMISSION)
                }
            }
        })
        alertBuilder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

            }
        })

        val alert = alertBuilder.create()
        alert.show()
    }

    private fun showARPermissionReasonable2() {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle(R.string.permission_rationale_title)
        alertBuilder.setMessage("This app needs access device storage and camera to enable 'AR' Visit Settings and Allow permissions")
        alertBuilder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        })
        alertBuilder.setNegativeButton("Cancel", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

            }
        })

        val alert = alertBuilder.create()
        alert.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQ_STORAGE_PERMISSION -> {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    copyVideo()
                    openAR()
                }
            }
            REQ_HELP_PERMISSION -> {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openHelp()
                }
            }
        }
    }

    private fun isNotificationServiceEnabled(): Boolean {
//        val pkgName = packageName
//        val flat = Settings.Secure.getString(contentResolver, ENABLED_NOTIFICATION_LISTENERS)
//        if (!TextUtils.isEmpty(flat)) {
//            val names = flat.split(":")
//            for (i in 0 until names.size) {
//                val cn = ComponentName.unflattenFromString(names[i])
//                if (cn != null) {
//                    if (TextUtils.equals(pkgName, cn.packageName)) {
//                        return true
//                    }
//                }
//            }
//        }
//        return false
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(packageName)
    }

    private fun showEditDialog() {
        val selectEditDialog = SelectEditDialog(this)
        selectEditDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        selectEditDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        selectEditDialog.show()
        selectEditDialog.setOnClickInfoListener(View.OnClickListener {
            startActivity(Intent(this, EditAccountActivity::class.java))
            selectEditDialog.dismiss()
        })
        selectEditDialog.setOnClickPasswordListener(View.OnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
            selectEditDialog.dismiss()
        })
    }

    override fun updateMyqueue(list: ArrayList<MyQueueResponse>) {
        val size = notificationList.size
        for (i in 0..size) {
            for (data in notificationList) {
                if (data is String) {
                    if (data == "คิวเข้ารับบริการของท่าน") {
                        notificationList.remove(data)
                        break
                    }
                } else if (data is MyQueueResponse) {
                    notificationList.remove(data)
                    break
                }
            }
        }

        val list2 = arrayListOf<Any>()
        list2.addAll(list)
        if (list.isNotEmpty()) {
            list2.add(0, "คิวเข้ารับบริการของท่าน")
        }
        for (data in notificationList) {
            list2.add(data)
        }

        notificationList.clear()
        notificationList = list2

        notificationAdapter.setData(notificationList)
        notificationAdapter.notifyDataSetChanged()
    }

    private fun showNotificationView() {
        notificationList = arrayListOf()
        //check queue
        presenter.getMyQueue()
        //update emer
        val emerList = if (Hawk.get<ArrayList<HashMap<String, String>>>(MyFirebaseMessagingService.NOTI_EMER) != null) {
            Hawk.get<ArrayList<HashMap<String, String>>>(MyFirebaseMessagingService.NOTI_EMER)
        } else {
            arrayListOf()
        }

        if (emerList.isNotEmpty()) {
            notificationList.add("เหตุฉุกเฉินที่ท่านแจ้ง")
            for (data in emerList) {
                notificationList.add(data)
            }
        }
        //update complain
        val complainList = if (Hawk.get<ArrayList<HashMap<String, String>>>(MyFirebaseMessagingService.NOTI_COMPLAIN) != null) {
            Hawk.get<ArrayList<HashMap<String, String>>>(MyFirebaseMessagingService.NOTI_COMPLAIN)
        } else {
            arrayListOf()
        }

        if (complainList.isNotEmpty()) {
            notificationList.add("เรื่องร้องเรียนของท่าน")
            for (data in complainList) {
                notificationList.add(data)
            }
        }

        if (checkNewNotification()) {
            notification_view.view_notification_new.visibility = View.VISIBLE
            notification_new.visibility = View.VISIBLE
            notification_toggle_new.visibility = View.VISIBLE
        } else {
            notification_view.view_notification_new.visibility = View.GONE
            notification_new.visibility = View.GONE
            notification_toggle_new.visibility = View.GONE
        }

        notificationAdapter.setData(notificationList)
        notificationAdapter.notifyDataSetChanged()
    }

    private fun checkNewNotification(): Boolean {
        val emerList = if (Hawk.get<ArrayList<HashMap<String, String>>>(MyFirebaseMessagingService.NOTI_EMER) != null) {
            Hawk.get<ArrayList<HashMap<String, String>>>(MyFirebaseMessagingService.NOTI_EMER)
        } else {
            arrayListOf()
        }

        val complainList = if (Hawk.get<ArrayList<HashMap<String, String>>>(MyFirebaseMessagingService.NOTI_COMPLAIN) != null) {
            Hawk.get<ArrayList<HashMap<String, String>>>(MyFirebaseMessagingService.NOTI_COMPLAIN)
        } else {
            arrayListOf()
        }

        return emerList.isNotEmpty() || complainList.isNotEmpty()
    }

    private fun showAccountView() {
        checkUserType()
    }

    private fun checkLayoutReady() {
        handler.removeCallbacksAndMessages(null)
        handler.postDelayed(runnable, 250)
    }

    private val runnable = Runnable {
        supportFragmentManager?.let {
            FragmentHelper.popToRootFragment(it)
            val currentFragment = FragmentHelper.getCurrentFragment(it, R.id.content_home_frame)
            if (currentFragment is HomeFragment) {
                onNewIntent(intent)
            } else {
                checkLayoutReady()
            }
        } ?: kotlin.run {
            checkLayoutReady()
        }
    }

    fun newIntent(intent: Intent?) {
        onNewIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        val extra = intent?.extras
        val type = extra?.getString("type") ?: ""
        if (type.contains("queue", true)) {
            supportFragmentManager?.let {
                FragmentHelper.popToRootFragment(it)
                val currentFragment = FragmentHelper.getCurrentFragment(it, R.id.content_home_frame)
                if (currentFragment is HomeFragment) {
                    currentFragment.moveToMenu(1)
                }
            }
        } else if (type.contains("news", true)) {
            supportFragmentManager?.let {
                FragmentHelper.popToRootFragment(it)
                val currentFragment = FragmentHelper.getCurrentFragment(it, R.id.content_home_frame)
                if (currentFragment is HomeFragment) {
                    currentFragment.moveToMenu(0)
                }
            }
        } else if (type.contains("complain", true)) {
            supportFragmentManager?.let {
                FragmentHelper.popToRootFragment(it)
                val currentFragment = FragmentHelper.getCurrentFragment(it, R.id.content_home_frame)
                if (currentFragment is HomeFragment) {
                    currentFragment.moveToMenu(2)
                    Hawk.put("click_complain", "true")
                }
            }
        } else if (type.contains("emergency", true)) {
            supportFragmentManager?.let {
                FragmentHelper.popToRootFragment(it)
                val currentFragment = FragmentHelper.getCurrentFragment(it, R.id.content_home_frame)
                if (currentFragment is HomeFragment) {
                    currentFragment.moveToMenu(2)
                    Hawk.put("click_emergency", "true")
                }
            }
        } else if (type.contains("q_rate", true)) {
            //show queue rate dialog
            supportFragmentManager?.let {
                FragmentHelper.popToRootFragment(it)
                val currentFragment = FragmentHelper.getCurrentFragment(it, R.id.content_home_frame)
                if (currentFragment is HomeFragment) {
                    currentFragment.moveToMenu(0)
                }
            }
            val data = extra?.getString("data") ?: ""
            val title = extra?.getString("title") ?: ""
            val intent = Intent(this, RatingDialogActivity::class.java)
            intent.putExtra("queue_checkin_id", data)
            intent.putExtra("title", title)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        this.unregisterReceiver(receiver)
        _instance = null
        super.onDestroy()
    }

    override fun onBackPressed() {
//        supportFragmentManager?.let {
//            val currentFragment = FragmentHelper.getCurrentFragment(it, R.id.content_home_frame)
//            if (currentFragment is HomeFragment) {
//                if (currentFragment.getCurrentTab() != 0) {
//                    currentFragment.moveToMenu(0)
//                } else {
//                    super.onBackPressed()
//                }
//            } else if (currentFragment is TicketDetailFragment) {
//                FragmentHelper.popToRootFragment(it)
//            } else {
//                super.onBackPressed()
//            }
//        } ?: kotlin.run {
//            super.onBackPressed()
//        }
//        when {
//            account_view.layout_slide_menu_account.isShown -> hideAccountView()
//            notification_view.layout_slide_menu.isShown -> hideNotificationView()
//            else -> {
        if (drawerLayout.isDrawerOpen(nav_view_right)) {
            drawerLayout.closeDrawer(nav_view_right)
            return
        }

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(baseContext, getString(R.string.alert_backpress), Toast.LENGTH_SHORT).show()
        }

        back_pressed = System.currentTimeMillis()
//            }
//        }
    }

    override fun updateNewsView(list: ArrayList<NewsResponse>) {
        hideLoading()
        newsList = list

        if (list.size > 0) {
            layout_news.visibility = View.VISIBLE
        }

        for (news in newsList) {
            presenter.getNewsImage(news)
        }
    }

    override fun updateImageList(news_id: String, list: ArrayList<NewsImgResponse.ImageData>) {
        imagecount += 1

        if (list.size > 0) {
            val imageList = arrayListOf<String>()
            for (data in list) {
                data.ImgUrl?.let {
                    imageList.add(it)
                }
            }
            for (data in newsList) {
                val id = data.NewsId ?: ""
                if (id == news_id && data.imgs == null) {
                    data.imgs = imageList
                }
            }
        }

        if (imagecount == newsList.size) {
            imagecount = 0
            newsPaggerAdapter.setData(newsList)
            newsPaggerAdapter.notifyDataSetChanged()
            newsPaggerAdapter.setClickItemListener(this)

            tab_layout.setupWithViewPager(news_viewpager)
            for (i in 0 until newsPaggerAdapter.count) {
                val tab = LayoutInflater.from(this).inflate(R.layout.custom_indicator, null)
                tab.setBackgroundResource(R.drawable.tab_selector)
                tab_layout.getTabAt(i)?.customView = tab
                tab_layout.getTabAt(i)?.customView?.layoutParams = LinearLayout.LayoutParams(resources.getDimensionPixelSize(R.dimen.dimen_tab_width), LinearLayout.LayoutParams.MATCH_PARENT)
            }
        }
    }

    override fun showError(message: String) {
        hideLoading()
        if (message.isNotEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onClickItem(data: NewsResponse) {
        val intent = Intent(this, NewsDetailActivity::class.java)
        val gson = GsonBuilder().create()
        val jsonData = gson.toJson(data)
        intent.putExtra("data", jsonData)
        startActivity(intent)
    }

    override fun updateCitizenInfo(data: CitizenInfoResponse.CitizenInfoResponseData) {
//        if (account_view.text_fname.text.isNullOrEmpty()) {
        account_view.text_fname.text = data.FName
        account_view.text_lname.text = data.LName

        account_view.market_profile_name.text = data.FName + " " + data.LName
//        }

        account_view.text_citno.text = data.IdCard
        if (account_view.text_email.text.isNullOrEmpty()) {
            account_view.text_email.text = data.Email
        }

        account_view.text_telno.text = data.Tel

        account_view.text_pro.text = data.ProvinceName
        account_view.text_aumphur.text = data.AmphueName
        account_view.text_tum.text = data.TambonName
        account_view.text_address.text = data.Address
        account_view.text_zip.text = data.ZipCode

        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        if (user?.authority_info?.IsFb != "1") {
            account_view.market_profile_image.loadCircle(AppUtils.getImageUrl(data.CitizenImg), R.mipmap.market_profile_image)
        }

        Hawk.put("user_image", data.CitizenImg.toString())
    }

    var sync = false

    private fun checkUserType() {
        val user = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        account_view.layout_edit.visibility = View.VISIBLE
        when {
            user.authority_info?.IsFb == "2" -> {
                account_view.layout_edit.isEnabled = false
                //Guest
                account_view.text_fname.text = "Guest"
                account_view.market_profile_name.text = "Guest"
                account_view.layout_edit.visibility = View.INVISIBLE
            }
            user.authority_info?.IsFb == "1" -> {
                account_view.layout_edit.isEnabled = true

//                account_view.text_email.text = "Facebook"

                val name = user.authority_info?.FName.toString() + " " + user.authority_info?.LName.toString()
                val accessToken = AccessToken.getCurrentAccessToken()
                val request = GraphRequest.newMeRequest(accessToken) { json, _ ->
                    // Application code
                    try {
                        val picture = json.getJSONObject("picture").getJSONObject("data").getString("url")
                        account_view.market_profile_image.loadCircle(picture, R.mipmap.market_profile_image)
                    } catch (exception: Exception) {
                    }
                    try {
                        val email = json.getString("email")
                        account_view.text_email.text = email.toString()
                    } catch (exception: Exception) {
                    }
                }
                val parameters = Bundle()
                parameters.putString("fields", "id,name,email,gender,birthday,picture.type(large)")
                request.parameters = parameters
                if (!sync) {
                    sync = !sync
                    request.executeAsync()
                }

                presenter.getCitizenInfo()
            }
            user.authority_info?.IsFb == "3" -> {
                account_view.layout_edit.isEnabled = true

                val name = user.authority_info?.FName.toString() + " " + user.authority_info?.LName.toString()
                val acct = Hawk.get<GAccount>("G_ACCOUNT")
                if (acct != null) {
                    account_view.market_profile_image.loadCircle(acct.photoUrl, R.mipmap.market_profile_image)
                    account_view.text_email.text = acct.email
                }
                presenter.getCitizenInfo()
            }
            else -> {
                account_view.layout_edit.isEnabled = true

                presenter.getCitizenInfo()
            }
        }
    }

    val filename = arrayListOf<String>().apply {
        add("Base_Horwote.wt3")
        add("Horwote_180320_final.wt3")
        add("Tree_Horwote_1.wt3")

        add("Aq_Bar.wt3")
        add("Aq_Build.wt3")
        add("Aq_Floor.wt3")
        add("Aq_Fream1_1.wt3")
        add("Aq_Fream1_2.wt3")
        add("Aq_Fream1_3.wt3")
        add("Aq_Fream2_1.wt3")
        add("Aq_Fream2_2.wt3")
        add("Aq_Fream2_3.wt3")
        add("aq2video.mp4")

        add("Building_Base.wt3")
        add("Building_Door_1.wt3")
        add("Building.wt3")

        add("Status_Base.wt3")
        add("Status_Door.wt3")
        add("Status_Prop.wt3")
        add("Status.wt3")
        add("Stage.wt3")
        add("Clock_Tower.wt3")
    }

    private fun isArReady(): Boolean {
        var isReady = true

        for (file in filename) {
            val path = Environment.getExternalStorageDirectory().absolutePath
            val f = File("$path/$file")
            if (!f.exists()) {
                isReady = false
                break
            }
        }
        return isReady
    }

    var downloadCounter = 0

    private fun download() {
        // instantiate it within the onCreate method
        val mProgressDialog = ProgressDialog(this)
        mProgressDialog.setMessage("Download " + filename[downloadCounter] + " " + (downloadCounter + 1) + "/" + filename.size)
        mProgressDialog.isIndeterminate = true
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        mProgressDialog.setCancelable(true)

        // execute this when the downloader must be fired
        downLoadExcecute(mProgressDialog)
    }

    private fun downLoadExcecute(mProgressDialog: ProgressDialog) {
        val downloadTask = com.transcode.smartcity101p2.utils.DownloadTask(this, filename[downloadCounter], mProgressDialog)
        downloadTask.setDownLoadListener {
            downloadCounter += 1
            if (downloadCounter < filename.size) {

                mProgressDialog.isIndeterminate = false
                mProgressDialog.max = 100
                mProgressDialog.progress = (downloadCounter * 100) / filename.size

                mProgressDialog.setMessage("Download " + filename[downloadCounter] + " " + downloadCounter + "/" + filename.size)
                downLoadExcecute(mProgressDialog)
            } else {
                downloadCounter = 0
                mProgressDialog.dismiss()
                openAR()
            }
        }

        downloadTask.execute("${Const.STATIC_STORE}${filename[downloadCounter]}")

        mProgressDialog.setOnCancelListener { downloadTask.cancel(true) }
    }

    private fun openAR() {

        if (!isArReady()) {
            showDownLoadPopup()
            return
        }

        val permissionManager = ArchitectView.getPermissionManager()
        val features = EnumSet.noneOf(Feature::class.java)
        features.add(Feature.IMAGE_TRACKING)
        val sampleData = SampleData.Builder("Multiple Targets", "03_MultipleTargets_1_MultipleTargets/index.html")
                .activityClass(SimpleArActivity::class.java)
                .extensions(arrayListOf())
                .arFeatures(features)
                .cameraPosition(CameraSettings.CameraPosition.BACK)
                .cameraResolution(CameraSettings.CameraResolution.AUTO)
                .camera2Enabled(false)
                .build()

        val status = ArchitectView.isDeviceSupporting(this, sampleData.getArFeatures())
        if (status.isSuccess) {
            sampleData.isDeviceSupporting(true, "")
        } else {
            sampleData.isDeviceSupporting(false, status.error.message)
        }

        val permissions = PermissionUtil.getPermissionsForArFeatures(sampleData.arFeatures)

        if (!sampleData.isDeviceSupporting) {
            showDeviceMissingFeatures(sampleData.isDeviceSupportingError)
        } else {
            permissionManager.checkPermissions(this, permissions, PermissionManager.WIKITUDE_PERMISSION_REQUEST, object : PermissionManager.PermissionManagerCallback {
                override fun permissionsGranted(requestCode: Int) {
                    val intent = Intent(this@HomeActivity, sampleData.activityClass)
                    intent.putExtra(SimpleArActivity.INTENT_EXTRAS_KEY_SAMPLE, sampleData)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }

                override fun permissionsDenied(deniedPermissions: Array<String>) {
                    Toast.makeText(this@HomeActivity, getString(R.string.permissions_denied) + Arrays.toString(deniedPermissions), Toast.LENGTH_SHORT).show()
                }

                override fun showPermissionRationale(requestCode: Int, strings: Array<String>) {
                    val alertBuilder = AlertDialog.Builder(this@HomeActivity)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle(R.string.permission_rationale_title)
                    alertBuilder.setMessage(getString(R.string.permission_rationale_text) + Arrays.toString(permissions))
                    alertBuilder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
                        override fun onClick(dialog: DialogInterface, which: Int) {
                            permissionManager.positiveRationaleResult(requestCode, permissions)
                        }
                    })

                    val alert = alertBuilder.create()
                    alert.show()
                }
            })
        }
    }

    private fun showDeviceMissingFeatures(errorMessage: String) {
        AlertDialog.Builder(this)
                .setTitle(R.string.device_missing_features)
                .setMessage(errorMessage)
                .show()
    }

    private fun openHelp() {
        try {
            val inp = assets.open("help.pdf")
            val outDir = Environment.getExternalStorageDirectory().absolutePath

            val outFile = File(outDir, "help.pdf")
//            if (outFile.createNewFile()) {
//                return
//            }
            val out = FileOutputStream(outFile)
            copyFile(inp, out)
            inp.close()
            out.flush()
            out.close()

            val intent = Intent(Intent.ACTION_VIEW)
            val uri = FileProvider.getUriForFile(this, "$packageName.provider", outFile)
            intent.setDataAndType(uri, "application/pdf")
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "cra: " + e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(inp: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read = inp.read(buffer)
        while (read != -1) {
            out.write(buffer, 0, read)
            read = inp.read(buffer)
        }
    }

    private fun copyVideo() {
        val inp = assets.open("samples/03_MultipleTargets_1_MultipleTargets/assets/video.mp4")
        val outDir = Environment.getExternalStorageDirectory().absolutePath

        val outFile = File(outDir, "video101.mp4")
//        if (outFile.createNewFile()) {
//            return
//        }
        val out = FileOutputStream(outFile)
        copyFile(inp, out)
        inp.close()
        out.flush()
        out.close()

        copyVideo2()
    }

    private fun copyVideo2() {
        val inp = assets.open("samples/03_MultipleTargets_1_MultipleTargets/assets/video_aq.mp4")
        val outDir = Environment.getExternalStorageDirectory().absolutePath

        val outFile = File(outDir, "video101_aq.mp4")
//        if (outFile.createNewFile()) {
//            return
//        }
        val out = FileOutputStream(outFile)
        copyFile(inp, out)
        inp.close()
        out.flush()
        out.close()
    }

    private fun buildNotificationServiceAlertDialog(): AlertDialog {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(getString(R.string.appplication_name))
        alertDialogBuilder.setMessage("Please Enable Notification Access")
        alertDialogBuilder.setPositiveButton(getString(R.string.ok_text), DialogInterface.OnClickListener { dialogInterface, i ->
            startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
        })
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel_text), DialogInterface.OnClickListener { dialogInterface, i ->
            finish()
        })
        return (alertDialogBuilder.create())
    }

    override fun onResume() {
        if (!isNotificationServiceEnabled()) {
            val dialog = buildNotificationServiceAlertDialog()
            dialog.show()
        }
        updateComplainLayout()
        updateEmerLayout()

        val updateNotification = UpdateNotification()
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        updateNotification.updateComplain(login?.authority_info?.CitizenId ?: "", this)
        updateNotification.updateEmergency(login?.authority_info?.CitizenId ?: "", this)

        super.onResume()
    }

    private fun updateEmerLayout() {
        val emer_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.EMER_CHAT_DB)
                ?: arrayListOf()
        var count = 0

        for (data in emer_db) {
            if (!data["emer_id"].isNullOrEmpty()) {
                count += (data["count"] ?: "0").toInt()
            }
        }

        if (count <= 0) {
            emer_count.visibility = View.GONE
            emer_count.text = ""
        } else {
            emer_count.visibility = View.VISIBLE
            emer_count.text = count.toString()
        }
    }

    private fun updateComplainLayout() {
        val complain_db = Hawk.get<ArrayList<HashMap<String, String>>>(Const.COMPLAIN_CHAT_DB)
                ?: arrayListOf()
        var count = 0

        for (data in complain_db) {
            if (!data["complain_id"].isNullOrEmpty()) {
                count += (data["count"] ?: "0").toInt()
            }
        }

        if (count <= 0) {
            complain_count.visibility = View.GONE
            complain_count.text = ""
        } else {
            complain_count.visibility = View.VISIBLE
            complain_count.text = count.toString()
        }
    }

    inner class HomeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateComplainLayout()
            updateEmerLayout()
        }
    }

    private fun showSSPopup() {
        var customDialog = CustomDialog(this)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        customDialog.show()
        customDialog.setTitle(getString(R.string.appplication_name))
        customDialog.setMessage("Open or Download \"Smart Sratong\" App")
        customDialog.setOnClickOKListener(View.OnClickListener {
            val appPackageName = "com.smartsratong.smartsratong"
            val launchIntent = packageManager.getLaunchIntentForPackage(appPackageName)
            launchIntent?.let {
                startActivity(it)
            } ?: kotlin.run {
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
                } catch (anfe: android.content.ActivityNotFoundException) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
                }
            }
            customDialog.dismiss()
        })

        customDialog.setOnClickCancelListener(View.OnClickListener {
            customDialog.dismiss()
        })
    }

    private fun showDownLoadPopup() {
        var customDialog = CustomDialog(this)
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        customDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        customDialog.show()
        customDialog.setTitle(getString(R.string.appplication_name))
        customDialog.setMessage("Download AR file to using this function")
        customDialog.setOnClickOKListener(View.OnClickListener {
            download()
            customDialog.dismiss()
        })

        customDialog.setOnClickCancelListener(View.OnClickListener {
            customDialog.dismiss()
        })
    }

    private fun showLocationPermissionReasonable(startIntent: Intent) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(false)
        alertBuilder.setTitle("Use your location")
        alertBuilder.setMessage("This app collects location data to see your current location in map [" + getString(R.string.home_emergency) + "] , " +
                "[" + getString(R.string.home_complain) + "] , " +
                "[" + getString(R.string.home_cctv) + "] & " +
                "[" + getString(R.string.home_travel) + "] even when the app is closed or not in use.")
        alertBuilder.setPositiveButton("OK", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {
                startActivity(startIntent)
            }
        })

        alertBuilder.setNegativeButton("No thanks", object : DialogInterface.OnClickListener {
            override fun onClick(dialog: DialogInterface, which: Int) {

            }
        })

        val alert = alertBuilder.create()
        alert.show()
    }

    private fun checkLocationPermission(startIntent: Intent) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
//                    shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_COARSE_LOCATION)) {
//                showLocationPermissionReasonable(startIntent)
//            } else {
//                startActivity(startIntent)
//            }
//        } else {
            showLocationPermissionReasonable(startIntent)
//        }
//        startActivity(startIntent)
    }
}
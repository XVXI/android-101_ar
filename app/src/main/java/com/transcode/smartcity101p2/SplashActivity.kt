package com.transcode.smartcity101p2

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.facebook.login.LoginManager
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.firebase.FirebaseMessagingManager
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.notification.KEY_NOTIFICATION_ID
import java.io.IOException

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mock)
        val inte = intent
        if (inte.extras != null) {
            val notiid = inte.extras.getString(KEY_NOTIFICATION_ID)
            if (notiid != null) {
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notiid.toInt())
            }
        }
        if (HomeActivity.getInstance() != null) {
            startApp(0)
        } else {
            checkPermission()
        }
    }

    private fun checkPermission() {
        this?.let {
            startApp(2000)
        }
    }

    private fun startApp(delay: Long) {
        Handler().postDelayed({
            val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
            val intent = if (login != null) {
                if (login.authority_info?.ArticleNewsCatId.isNullOrEmpty() || login.authority_info?.YoutubeList.isNullOrEmpty()) {
                    val mFirebaseMessaging = FirebaseMessaging.getInstance()
                    val city_id_text = if (login.authority_info?.CityId.isNullOrEmpty()) {
                        ""
                    } else {
                        "_" + login.authority_info?.CityId
                    }
                    mFirebaseMessaging.unsubscribeFromTopic(FirebaseMessagingManager.KEY_FIREBASE_TOPIC + city_id_text)
                    mFirebaseMessaging.unsubscribeFromTopic(FirebaseMessagingManager.KEY_FIREBASE_TOPIC_NEWS + city_id_text)

                    val notificationIdList = if (Hawk.get<ArrayList<Int>>("noti_id_list") != null) {
                        Hawk.get<ArrayList<Int>>("noti_id_list")
                    } else {
                        arrayListOf()
                    }
                    Hawk.delete("noti_id_list")
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    for (noti_id in notificationIdList) {
                        notificationManager.cancel(noti_id)
                    }

                    LoginManager.getInstance().logOut()
                    CoreApplication.deleteLoginData()
                    Intent(this, LoginActivity::class.java)
                } else {
                    Intent(this, HomeActivity::class.java)
                }
            } else {
                LoginManager.getInstance().logOut()
                Intent(this, LoginActivity::class.java)
            }

            val inte = getIntent()
            if (inte.extras != null) {
                intent.putExtras(inte.extras)
            }

            startActivity(intent)
            finish()
        }, delay)
    }

    fun isGrantedPermission(): Boolean {
        this.let {
            return (ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(it, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            111 -> {
                if (isGrantedPermission()) {
                    startApp(2000)
                } else {
                    cleanAppByCmd()
                    finish()
                }
            }
            else -> {
                cleanAppByCmd()
                finish()
            }
        }
    }

    private fun cleanAppByCmd() {
        val TAG = "TAG_cleanAppByCmd"
        val cmd = "pm clear " + getAppPackageName()
        val runtime = Runtime.getRuntime()
        try {
            runtime.exec(cmd)
//            Log.d(TAG, "Success")
        } catch (e: IOException) {
            e.printStackTrace()
//            Log.d(TAG, "Failure")
        }
    }

    private fun getAppPackageName(): String {
        return BuildConfig.APPLICATION_ID
    }

    override fun onNewIntent(intent: Intent?) {
//        Log.e("intent", intent?.extras.toString())
        super.onNewIntent(intent)
    }
}
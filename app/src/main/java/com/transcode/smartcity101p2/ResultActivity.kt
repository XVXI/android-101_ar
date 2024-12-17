package com.transcode.smartcity101p2

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.notification.KEY_NOTIFICATION_ID

class ResultActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        if (login.authority_info?.CitizenId.isNullOrEmpty()) {
            val splash = Intent(this, SplashActivity::class.java)
            splash.putExtras(intent.extras)
            startActivity(splash)
        } else {
            val inte = intent
            if (inte.extras != null) {
                val notiid = inte.extras.getString(KEY_NOTIFICATION_ID)
                if (notiid != null) {
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notiid.toInt())
                }
            }
            val homeIntent = Intent(this, HomeActivity::class.java)
            if (inte.extras != null) {
                homeIntent.putExtras(inte.extras)
            }
            startActivity(homeIntent)

        }

        if (!Hawk.isBuilt()) {
            Hawk.init(this)
        }
        Hawk.put("by_noti", true)

        finish()
    }
}
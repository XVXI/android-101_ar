package com.transcode.smartcity101p2

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.support.multidex.MultiDex
import android.text.TextUtils
import android.util.DisplayMetrics
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import com.mapbox.mapboxsdk.Mapbox
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.firebase.FirebaseMessagingManager
import com.transcode.smartcity101p2.fragment.CoreFragment
import com.transcode.smartcity101p2.model.CityResponse
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import java.util.*


const val PREF_NAME = "LanguageData"
const val PREF_LANG = "language"
const val PREF_LANG_EN = "en"
const val PREF_LANG_TH = "th"
const val PREF_LANG_ZH = "zh"
const val PRIVATE_MODE = 0

class CoreApplication : Application(), Application.ActivityLifecycleCallbacks {

    companion object {
        private var currentFragment: CoreFragment? = null
        private var appLanguage: String = ""
        private var screen_width = 0
        private var screen_height = 0

        fun setCurFragment(currentFragment: CoreFragment?) {
            Companion.currentFragment = currentFragment
        }

        fun getCurFragment(): CoreFragment? = currentFragment

        fun forceLanguage(context: Context, local: String) {
            val locale = when (local) {
                "zh" -> Locale.CHINA
                "en" -> Locale.ENGLISH
                else -> Locale(local)
            }

            val activityRes = context.resources
            val activityConf = activityRes.configuration
            activityConf.setLocale(locale)
            activityRes.updateConfiguration(activityConf, activityRes.displayMetrics)

            val applicationRes = context.applicationContext.resources
            val applicationConf = applicationRes.configuration
            applicationConf.setLocale(locale)
            applicationRes.updateConfiguration(applicationConf,
                    applicationRes.displayMetrics)
        }

        fun getLanguage(context: Context): String {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            appLanguage = pref.getString(PREF_LANG, "") ?: ""

            appLanguage = if (appLanguage.contains("en", ignoreCase = true)) {
                PREF_LANG_EN
            } else if (appLanguage.contains("zh", ignoreCase = true)) {
                PREF_LANG_ZH
            } else {
                PREF_LANG_TH
            }

            return appLanguage
        }

        fun setLanguage(context: Context, language: String) {
            val pref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
            val editor: SharedPreferences.Editor = pref.edit()
            editor.putString(PREF_LANG, language)
            editor.apply()
            editor.commit()
            appLanguage = language
        }

        fun openActivity(context: Context, intent: Intent) {
            context.startActivity(intent)
        }

        fun saveLoginData(res: LoginResponse) {
            Hawk.put(Const.KEY_LOGIN_DATA, res)
        }

        fun deleteLoginData() {
            Hawk.delete(Const.KEY_LOGIN_DATA)

            //shop
            Hawk.delete("DEF_ADDRESS")
        }

        fun getUDID(context: Context): String {
            val res = Hawk.get<CityResponse>(Const.KEY_CITY)
            val id = res?.Cityid ?: ""
            return android.provider.Settings.System.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID) + id
        }


        fun getScreenWidth(): Int {
            return screen_width
        }

        fun getScreenHeight(): Int {
            return screen_height
        }
    }

    lateinit var firebaseMessaging: FirebaseMessaging

    override fun onCreate() {
        super.onCreate()

        firebaseMessaging = FirebaseMessagingManager().build()

        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))

        registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityPaused(p0: Activity?) {

    }

    override fun onActivityResumed(p0: Activity?) {

    }

    override fun onActivityStarted(p0: Activity?) {

    }

    override fun onActivityDestroyed(p0: Activity?) {

    }

    override fun onActivitySaveInstanceState(p0: Activity?, p1: Bundle?) {

    }

    override fun onActivityStopped(p0: Activity?) {

    }

    override fun onActivityCreated(activity: Activity, saveInstance: Bundle?) {
        val dm = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(dm)
        screen_width = dm.widthPixels
        screen_height = dm.heightPixels

        forceLanguage(activity, getLanguage(activity))
        Thread.setDefaultUncaughtExceptionHandler(CrashReport())
        Hawk.init(activity).build()

        object : AsyncTask<Void, Void, String>() {
            override fun doInBackground(vararg params: Void): String {
                return try {
                    FirebaseInstanceId.getInstance().getToken("338578754053", "FCM")
                } catch (e: Exception) {
                    ""
                }
            }

            override fun onPostExecute(msg: String) {
                Hawk.put("FCM_TOKEN", msg)
            }
        }.execute(null, null, null)


        clearDB()
    }

    private fun clearDB() {
        Hawk.delete(Const.KEY_CITY)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
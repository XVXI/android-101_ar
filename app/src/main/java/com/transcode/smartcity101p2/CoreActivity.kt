package com.transcode.smartcity101p2

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.dialog.LoadingDialog
import com.transcode.smartcity101p2.firebase.FirebaseMessagingManager
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.view.CustomAppBarLayout

open class CoreActivity : AppCompatActivity() {
    fun getAppBarLayout(view: View): CustomAppBarLayout? = view.findViewById(R.id.appbar)

    var dialogLoading: LoadingDialog? = null

    fun showLoading() {
        dialogLoading?.let {
            it.show()
        } ?: kotlin.run {
            dialogLoading = LoadingDialog(this)
            dialogLoading?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialogLoading?.window?.setBackgroundDrawableResource(android.R.color.transparent)
            dialogLoading?.setCancelable(false)
            dialogLoading?.show()
        }
    }

    fun hideLoading() {
        dialogLoading?.dismiss()
    }

    fun hideSoftKeyboard() {
        try {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun logOut() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        val mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        mGoogleSignInClient?.signOut()?.addOnCompleteListener {
            //            Log.e("logout", "logout")
        }
        val mFirebaseMessaging = FirebaseMessaging.getInstance()
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
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

        Hawk.delete("G_ACCOUNT")
        Hawk.delete("user_image")
        Hawk.delete(Const.EMER_CHAT_DB)
        Hawk.delete(Const.COMPLAIN_CHAT_DB)
        LoginManager.getInstance().logOut()
        CoreApplication.deleteLoginData()
        finish()
        CoreApplication.openActivity(this, Intent(this, LoginActivity::class.java))
    }
}
package com.transcode.smartcity101p2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.WindowManager
import com.google.gson.Gson
import com.orhanobut.hawk.Hawk
import com.transcode.smartcity101p2.fragment.ComplainDetailFragment
import com.transcode.smartcity101p2.fragment.FragmentHelper
import com.transcode.smartcity101p2.model.Const
import com.transcode.smartcity101p2.model.LoginResponse
import com.transcode.smartcity101p2.model.complain.ComplainListResponse
import com.transcode.smartcity101p2.services.UpdateNotification

class ComplainDetailActivity : CoreActivity() {

    lateinit var fragment: ComplainDetailFragment
    private val receiver = ComplainDetailReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_complain)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        val filter = IntentFilter(Const.BROADCAST_TOGGLE_NOTIFICATION)
        this.registerReceiver(receiver, filter)

        intent?.extras?.let {
            val gson = Gson()
            val data = gson.fromJson(it.getString("data"), ComplainListResponse.ComplainListResponseData::class.java)
            fragment = ComplainDetailFragment.newInstance(data, it.getBoolean("open_chat"))
            FragmentHelper.replaceWithoutAddingToBackStack(supportFragmentManager, fragment, R.id.content_frame)
        }
    }

    override fun onDestroy() {
        this.unregisterReceiver(receiver)
        super.onDestroy()
    }

    override fun onBackPressed() {
        fragment.onBackPressed()
    }

    inner class ComplainDetailReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            fragment.updateNotification()
        }
    }

    override fun onResume() {
        fragment.updateNotification()

        val updateNotification = UpdateNotification()
        val login = Hawk.get<LoginResponse>(Const.KEY_LOGIN_DATA)
        updateNotification.updateComplain(login?.authority_info?.CitizenId ?: "", this)
        updateNotification.updateEmergency(login?.authority_info?.CitizenId ?: "", this)

        super.onResume()
    }
}